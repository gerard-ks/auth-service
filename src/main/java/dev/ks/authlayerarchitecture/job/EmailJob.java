package dev.ks.authlayerarchitecture.job;

import java.util.Map;

public record EmailJob(
        String              templateName,
        String              to,
        String              subject,
        Map<String, Object> variables
) {}
