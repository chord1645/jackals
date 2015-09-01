package com.shrek.crawler.test.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class ConsumerTest1 implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
 
    public ConsumerTest1(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> mnm = it.next();
            System.out.println("partition " + mnm.partition() + ": " + new String(mnm.message()));
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}