package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;

public class Logger {
    public enum Action {
        BEGIN, RETURN
    };

    org.apache.logging.log4j.Logger log;

    private static final String BASE_DIR = "/Users/rwiles";
    private static final boolean WRITE_SEQUENCE_DIAGRAMS = false;

    private static Map<Thread, Stack<String>> classCallStack = new HashMap<>();;
    private static Map<Thread, Stack<String>> threadMethodCallStack = new HashMap<>();;
    private static Map<Thread, String> lastReturnedValueNames = new HashMap<>();
    private static Map<Thread, ArrayList<SequenceCall>> activeSequences = new HashMap<>();
    private static Map<Thread, String> scenarioNames = new HashMap<>();

    public Logger(Class<?> clazz) {
        log = LogManager.getLogger(clazz);
    }

    String getLastReturnedValueNames() {
        return lastReturnedValueNames.getOrDefault(Thread.currentThread(), "");
    }

    public void setScenarioName(String name) {
        Thread thread = Thread.currentThread();
        scenarioNames.put(thread, name);
    }

    private int findMatchingReturnIndex(List<SequenceCall> seqs, int currSeqIndex) {
        SequenceCall seq = seqs.get(currSeqIndex);

        if (seq.isCall) {
            int stackDepth = 0;
            for (int j = currSeqIndex; j < seqs.size(); j++) {
                if (seqs.get(j).isCall) stackDepth++;
                else stackDepth--;
                if (stackDepth == 0) {
                    return j;
                }
            }
        }
        return -1;
    }

    void logSequenceCalls() {

        Thread thread = Thread.currentThread();

        if (WRITE_SEQUENCE_DIAGRAMS) {
            String dirName = BASE_DIR + File.separator + "sequenceDiagrams";
            File sequenceDiagramDirectory = new File(dirName);
            if (!sequenceDiagramDirectory.exists()) sequenceDiagramDirectory.mkdir();

            String scenarioName = scenarioNames.getOrDefault(thread, thread.getId() + "_" + System.currentTimeMillis());
            List<SequenceCall> seqs = activeSequences.getOrDefault(thread, new ArrayList<SequenceCall>());

            Set<String> names = new LinkedHashSet<>();
            names.add("user");
            names.add("webBrowser");
            for (SequenceCall seq : seqs) {
                names.add(seq.calledClass);
            }

            File seqLog = new File(dirName + File.separatorChar + scenarioName + ".sd");

            StringBuffer sb = new StringBuffer();
            sb.append("#![" + scenarioName + "]\n");
            for (String name : names) {
                if ("user".equals(name)) sb.append(name).append(":").append("Actor").append("\n");
                else if ("webBrowser".equals(name)) sb.append(name).append(":").append("WebBrowser").append("\n");
                else sb.append(name).append(":").append(name).append("[a]\n");
            }
            sb.append("\n");

            sb.append("user:webBrowser.\n");

            Map<String, Integer> classStackDepth = new HashMap<>();
            String actualClassMethod = "";
            String actualFromClassMethod = "";
            String prevGraphClass = "";
            String prevGraphClassMethod = "";

            if (!seqs.isEmpty()) seqs.get(0).calledFromClass = "webBrowser";

            for (int i = 0; i < seqs.size(); i++) {
                SequenceCall seq = seqs.get(i);

                if (seq.isCall) {
                    actualClassMethod = seq.calledClass + "." + seq.calledMethod;
                    actualFromClassMethod = seq.calledFromClass + "." + seq.calledFromMethod;
                    classStackDepth.put(seq.calledClass, classStackDepth.getOrDefault(seq.calledClass, 0) + 1);
                } else {
                    actualClassMethod = seq.calledFromClass + "." + seq.calledFromMethod;
                    classStackDepth.put(seq.calledClass, classStackDepth.get(seq.calledClass) - 1);
                }

                sb.append("#    [" + i + "]: " + seq.toString() + "\n");
                if (seq.isCall) {
                    int retSeqIndex = findMatchingReturnIndex(seqs, i);
                    if (retSeqIndex != -1) sb.append("#         Returns @ index " + retSeqIndex + "\n");
                }

                if (seq.isCall) {
                    String graphClassMethod = actualClassMethod;

                    if (seq.isCall) classStackDepth.put(seq.calledClass, classStackDepth.getOrDefault(seq.calledClass, 0) + 1);

                    int retSeqIndex = findMatchingReturnIndex(seqs, i);
                    SequenceCall retSeq = seqs.get(retSeqIndex);

                    if (!names.contains(seq.calledFromClass)) sb.append("webBrowser");
                    else sb.append(seq.calledFromClass);

                    if (seq.calledFromClass.equals(prevGraphClass) && !actualFromClassMethod.equals(prevGraphClassMethod)) sb.append("[1]");

                    sb.append(":");
                    if (retSeq != null) sb.append(retSeq.returnedVal).append("=");
                    sb.append(seq.calledClass).append(".").append(seq.calledMethod);
                    sb.append("\n\n");

                    prevGraphClass = seq.calledClass;
                    prevGraphClassMethod = graphClassMethod;
                }
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(seqLog));
                bw.write(sb.toString());
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        classCallStack.remove(thread);
        threadMethodCallStack.remove(thread);
        lastReturnedValueNames.remove(thread);
        activeSequences.remove(thread);
        scenarioNames.remove(thread);
    }

    public void log(Action action, String... parameterOrReturnValueNames) {

        Thread thread = Thread.currentThread();

        StackTraceElement[] elements = new Exception().getStackTrace();

        StackTraceElement ste = elements[1];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();

        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) lastIndex = 0;
        else lastIndex++;
        className = className.substring(lastIndex, className.length());

        String methodNameWithParams = methodName;

        Stack<String> classStack = classCallStack.computeIfAbsent(thread, t -> new Stack<>());
        Stack<String> methodStack = threadMethodCallStack.computeIfAbsent(thread, t -> new Stack<>());
        String callersClass = null;
        String callersMethod = null;

        if (Action.BEGIN == action) {
            if (parameterOrReturnValueNames != null && parameterOrReturnValueNames.length > 0) {
                methodNameWithParams += "(" + implode(parameterOrReturnValueNames) + ")";
            }

            if (!classStack.empty()) callersClass = classStack.peek();
            if (!methodStack.empty()) callersMethod = methodStack.peek();

            classStack.push(className);
            methodStack.push(methodNameWithParams);

            activeSequences.computeIfAbsent(thread, t -> new ArrayList<>()).add(new SequenceCall(true, callersClass, callersMethod, className, methodNameWithParams, null));

        } else if (Action.RETURN == action) {

            classCallStack.get(thread).pop();
            methodNameWithParams = threadMethodCallStack.get(thread).pop();

            callersClass = null;
            callersMethod = null;
            if (!classStack.empty()) callersClass = classStack.peek();
            if (!methodStack.empty()) callersMethod = methodStack.peek();

            activeSequences.computeIfAbsent(thread, t -> new ArrayList<>()).add(new SequenceCall(false, callersClass, callersMethod, className, methodNameWithParams, implode(parameterOrReturnValueNames)));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(action).append(" - ");
        sb.append(className).append(".");
        sb.append(methodNameWithParams);

        if (Action.BEGIN == action) {} else if (Action.RETURN == action) {
            String lastReturnedValueName = implode(parameterOrReturnValueNames);
            lastReturnedValueNames.put(thread, lastReturnedValueName);
            sb.append(" ").append(lastReturnedValueName);
        }

        log.info(sb.toString());
    }

    public void info(String message) {
        log.info(message);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Exception e) {
        log.error(message, e);
    }

    public void error(Exception e) {
        log.error(e);
    }

    private String implode(String... arr) {
        String retval = "";

        boolean addComma = false;
        for (String parameterName : arr) {
            if (addComma) retval += ", ";
            retval += parameterName;
            addComma = true;
        }
        return retval;
    }

    class SequenceCall {
        boolean isCall; // is this a call or a return
        String calledFromClass;
        String calledFromMethod;
        String calledClass;
        String calledMethod;
        String returnedVal;

        public SequenceCall(boolean isCall, String calledFromClass, String calledFromMethod, String calledClass, String calledMethod, String returnedVal) {
            super();
            this.isCall = isCall;
            this.calledFromClass = calledFromClass;
            this.calledFromMethod = calledFromMethod;
            this.calledClass = calledClass;
            this.calledMethod = calledMethod;
            this.returnedVal = returnedVal;
        }

        @Override
        public String toString() {
            if (isCall) return "call from " + calledFromClass + "." + calledFromMethod + " to " + calledClass + "." + calledMethod + ", returnedVal=" + returnedVal;
            else return "return to " + calledFromClass + "." + calledFromMethod + " from " + calledClass + "." + calledMethod + ", returnedVal=" + returnedVal;
        }
    }
}
