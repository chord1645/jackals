package jackals.mq;

import com.fasterxml.jackson.core.JsonParseException;
import kafka.message.MessageAndMetadata;

import java.io.IOException;

/**
 */
public interface MQListener {
    void requestReceived(String message) throws IOException;

    void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException;
//    void requestReceived(MessageAndMetadata<String, String> mnm);
}
