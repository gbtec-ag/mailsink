package com.github.ksokol.mailsink.bootstrap;

import com.github.ksokol.mailsink.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Kamill Sokol
 */
@Component
class ExampleMailApplicationRunner implements ApplicationRunner {

    private final MailRepository mailRepository;

    @Autowired
    private ExampleMails exampleMails;

    @Value("${mailsink.create-examples}")
    private Boolean createExamples;

    public ExampleMailApplicationRunner(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (createExamples) {
            exampleMails.listExampleMails().forEach(mailRepository::save);
        }
    }
}
