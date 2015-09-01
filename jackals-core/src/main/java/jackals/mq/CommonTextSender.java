package jackals.mq;

import jackals.model.RequestOjb;

import java.util.List;

abstract public class CommonTextSender {

    public abstract void sendOne(String topic, String msg);
//    public abstract void sendBatchRequest(String topic, List<String> msg);
    abstract public void sendBatchRequest(String topic, List<RequestOjb> list) ;



}