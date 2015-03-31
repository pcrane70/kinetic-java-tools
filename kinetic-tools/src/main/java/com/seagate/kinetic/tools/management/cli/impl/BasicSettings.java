package com.seagate.kinetic.tools.management.cli.impl;

abstract class BasicSettings {
    protected boolean useSsl;
    protected long clusterVersion;
    protected long identity;
    protected String key;
    protected long requestTimeout;

    protected void initBasicSettings(boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) {
        this.useSsl = useSsl;
        this.clusterVersion = clusterVersion;
        this.identity = identity;
        this.key = key;
        this.requestTimeout = requestTimeout;
    }
}
