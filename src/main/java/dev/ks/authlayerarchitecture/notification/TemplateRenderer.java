package dev.ks.authlayerarchitecture.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateRenderer {

    private final TemplateEngine templateEngine;

    public String render(
            String templateName,
            Map<String, Object> variables
    ) {
        Context context = new Context();
        context.setVariables(variables);

        try {
            return templateEngine.process(templateName, context);
        } catch (Exception ex) {
            log.error(
                    "Failed to render template [{}]",
                    templateName,
                    ex
            );
            throw new IllegalStateException(
                    "Failed to render email template: " + templateName,
                    ex
            );
        }
    }
}
