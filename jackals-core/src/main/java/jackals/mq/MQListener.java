package jackals.mq;

import com.fasterxml.jackson.core.JsonParseException;
import kafka.message.MessageAndMetadata;

import java.io.IOException;

/**
 */
public interface MQListener {
    void onReceived(String message) throws IOException;

    void onReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException;
//    void onReceived(MessageAndMetadata<String, String> mnm);
}
