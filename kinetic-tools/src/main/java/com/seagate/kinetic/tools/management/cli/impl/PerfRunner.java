package com.seagate.kinetic.tools.management.cli.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import kinetic.client.KineticException;

public class PerfRunner extends DefaultExecuter {
    private static final String KINETIC_TOOLS_HOME = System.getProperty("KINETIC_TOOLS_HOME", ".");
    private static final String DEFAULT_WORKLOAD_KINETIC_PATH = "workloadkinetic";
    private static final int ZERO = 0;
    private String valueSize;
    private String recordCount;
    private String operationCount;
    private String connectionPerDrive;
    private String readProportion;
    private String insertProportion;
    private String distribution;
    private String threads;

    public PerfRunner(String driveListInputFile, String valueSize,
            String recordCount, String operationCount,
            String connectionPerDrive, String readProportion,
            String insertProportion, String distribution, String threads) throws IOException {
        loadDevices(driveListInputFile);
        this.valueSize = valueSize;
        this.recordCount = recordCount;
        this.operationCount = operationCount;
        this.connectionPerDrive = connectionPerDrive;
        this.readProportion = readProportion;
        this.insertProportion = insertProportion;
        this.distribution = distribution;
        this.threads = threads;
    }

    public void performanceRunner() throws Exception {
        String workloadContent = assembleWorkload(devices, valueSize,
                recordCount, operationCount, connectionPerDrive,
                readProportion, insertProportion, distribution);

        persistWorkloadFile(workloadContent, DEFAULT_WORKLOAD_KINETIC_PATH);

        String ycsbScriptPath = KINETIC_TOOLS_HOME + File.separator + "bin" + File.separator + "run_ycsb.sh";
        String commandLine = ycsbScriptPath + " " + threads; 
        
        System.out.println(commandLine);
        
        runCommandLine(commandLine);
    }

    private void runCommandLine(String commandLine) throws Exception {
        Process p0 = Runtime.getRuntime().exec(commandLine);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(p0.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        BufferedReader brError = new BufferedReader(new InputStreamReader(
                p0.getErrorStream(), "utf8"));
        String errline = null;
        while ((errline = brError.readLine()) != null) {
            System.out.println(errline);
        }

        int c = p0.waitFor();
        if (c != 0) {
            throw new Exception("Excute Shell Command line failed.");
        }
    }

    private String assembleWorkload(List<KineticDevice> kineticDevices,
            String valueSize, String recordCount, String operationCount,
            String connectionPerDrive, String readProportion,
            String insertProportion, String distribution)
            throws KineticException {
        StringBuffer sb = new StringBuffer();
        String hosts = assembleDevices(kineticDevices);
        sb.append(hosts);
        sb.append("\n");

        sb.append("fieldcount=1");
        sb.append("\n");

        sb.append("connectionpernode=" + connectionPerDrive);
        sb.append("\n");

        sb.append("fieldlength=" + valueSize);
        sb.append("\n");

        sb.append("recordcount=" + recordCount);
        sb.append("\n");

        sb.append("operationcount=" + operationCount);
        sb.append("\n");

        sb.append("workload=com.yahoo.ycsb.workloads.CoreWorkload");
        sb.append("\n");

        sb.append("readallfields=true");
        sb.append("\n");

        sb.append("readproportion=" + readProportion);
        sb.append("\n");

        sb.append("insertproportion=" + insertProportion);
        sb.append("\n");

        sb.append("updateproportion=" + ZERO);
        sb.append("\n");

        sb.append("scanproportion=" + ZERO);
        sb.append("\n");

        sb.append("requestdistribution=" + distribution);

        return sb.toString();
    }

    private void persistWorkloadFile(String workloadKinetic,
            String workloadFilePath) throws KineticException, IOException {
        if (null == workloadFilePath || null == workloadKinetic) {
            throw new KineticException(
                    "Workload file content is null or persist path is null");
        }

        FileOutputStream fos = new FileOutputStream(new File(workloadFilePath));

        fos.write(workloadKinetic.getBytes());
        fos.flush();
        fos.close();
    }

    private String assembleDevices(List<KineticDevice> kineticDevices)
            throws KineticException {
        StringBuffer sb = new StringBuffer();
        if (null == kineticDevices || kineticDevices.isEmpty()
                || 0 == kineticDevices.size()) {
            throw new KineticException(
                    "Drives info get from input file is null or empty.");
        }

        sb.append("hosts=");
        
        for (KineticDevice kineticDevice : kineticDevices) {
            String host = null;
            if (null != kineticDevice && null != kineticDevice.getInet4()
                    && 0 != kineticDevice.getInet4().size()) {
                if (null != kineticDevice.getInet4().get(0)) {
                    host = kineticDevice.getInet4().get(0);
                } else {
                    host = kineticDevice.getInet4().get(1);
                }
            }

            String port = String.valueOf(kineticDevice.getPort());

            sb.append(host);
            sb.append(":");
            sb.append(port);
            sb.append(";");
        }

        return sb.toString();
    }
}
