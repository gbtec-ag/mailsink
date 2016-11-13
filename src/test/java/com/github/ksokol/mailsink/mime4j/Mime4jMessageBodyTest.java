package com.github.ksokol.mailsink.mime4j;

import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.message.MessageBuilder;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * @author Kamill Sokol
 */
public class Mime4jMessageBodyTest {

    private static final byte[] BODY = new byte[] { (byte) 'a' };

    private Mime4jMessageBody body;

    @Test
    public void shouldExtractPlainTextPart() throws Exception {
        givenMessage("plain1");

        assertThat(body.getPlainTextPart(), is(format("Mail body%nnew line%n")));
        assertThat(body.getHtmlTextPart(), is(""));
    }

    @Test
    public void shouldExtractHtmlTextPart() throws Exception {
        givenMessage("html1");

        assertThat(body.getHtmlTextPart(), is(format("<html>%n<body>%n<p>html mail</p>%n</body>%n</html>%n")));
        assertThat(body.getPlainTextPart(), is(""));
    }

    @Test
    public void shouldNotContainAttachments() throws Exception {
        givenMessage("plain1");

        assertThat(body.getAttachments(), hasSize(0));
    }

    @Test
    public void shouldContainPlainTextPartAndPdfAttachment() throws Exception {
        givenMessage("plain1_attachment");

        assertThat(body.getPlainTextPart(), is(format("This is a plain text message with a PDF attachment.%n")));
        assertThat(body.getAttachments(), hasSize(1));

        Mime4jAttachment attachment = body.getAttachments().get(0);

        assertThat(attachment.getFilename(), is("example.pdf"));
        assertThat(attachment.getMimeType(), is("application/pdf"));
        assertThat(attachment.getData(), equalTo(BODY));
    }

    @Test
    public void shouldContainHtmlTextPartAndPdfAttachment() throws Exception {
        givenMessage("html1_attachment");

        assertThat(body.getHtmlTextPart(), is(format("<html>%n<body>%n<p>html mail</p>%n</body>%n</html>%n")));
        assertThat(body.getAttachments(), hasSize(1));

        Mime4jAttachment attachment = body.getAttachments().get(0);

        assertThat(attachment.getFilename(), is("example.pdf"));
        assertThat(attachment.getMimeType(), is("application/pdf"));
        assertThat(attachment.getData(), equalTo(BODY));
    }

    @Test
    public void shouldContainPlainTextPartAndPlainTextBodyOfNestedMessage() throws Exception {
        givenMessage("nested1_mail");

        assertThat(body.getPlainTextPart(), is("nested message"));
        assertThat(body.getHtmlTextPart(), is(""));

        assertThat(body.getAttachments(), hasSize(1));
        Mime4jAttachment attachment = body.getAttachments().get(0);

        assertThat(attachment.getFilename(), is("Disposition Notification Test.txt"));
        assertThat(attachment.getMimeType(), is("text/plain"));
        assertThat(attachment.getData(), equalTo(BODY));
    }

    @Test
    public void shouldContainPlainTextPartAndHtmlTextBodyOfNestedMessage() throws Exception {
        givenMessage("nested2_mail");

        assertThat(body.getPlainTextPart(), is("nested message"));
        assertThat(body.getHtmlTextPart(), is(""));

        assertThat(body.getAttachments(), hasSize(1));
        Mime4jAttachment attachment = body.getAttachments().get(0);

        assertThat(attachment.getFilename(), is("<53FEB7F0.3030501@localhost>.html"));
        assertThat(attachment.getMimeType(), is("text/html"));
        assertThat(attachment.getData(), equalTo(BODY));
    }

    @Test
    public void shouldDecodeFilenameAccordingToRFC2231() throws Exception {
        givenMessage("plain2_attachment");

        assertThat(body.getAttachments(), hasSize(1));
        Mime4jAttachment attachment = body.getAttachments().get(0);

        assertThat(attachment.getFilename(), is("example.pdf"));
    }

    @Test
    public void shouldContainPlainTextPartAndHtmlTextPart() throws Exception {
        givenMessage("alternative1");

        assertThat(body.getHtmlTextPart(), is("<html><body><p>html mail</p></body></html>"));
        assertThat(body.getPlainTextPart(), is("Mail body"));
        assertThat(body.getAttachments(), hasSize(0));
    }

    @Test
    public void shouldContainPlainTextPartAndNoNestedMultipartMessage() throws Exception {
        givenMessage("nested3_mail");

        assertThat(body.getHtmlTextPart(), is(""));
        assertThat(body.getPlainTextPart(), is("nested message"));
        assertThat(body.getAttachments(), hasSize(0));
    }

    private void givenMessage(String fileName) throws Exception {
        InputStream inputStream = new ClassPathResource(format("mime4j/%s.eml", fileName)).getInputStream();
        Message message = new MessageBuilder().parse(inputStream).build();
        body = new Mime4jMessageBody(message);
    }

}