package mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.ArrayList;
import java.util.List;


//To execute mosquitto on server.
public class MQTTSubscriber implements MqttCallback{

	List<Float> buffer = new ArrayList<Float>();
	
    // the broker URL
    private static final String BROKER_URL = "tcp://localhost:1883";

    // init the client
    private MqttClient mqttClient;

    /**
     * Constructor. It generates a client id and instantiate the MQTT client.
     */
    public MQTTSubscriber() {

        try {
            mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method to start the subscriber. It listen to all the homestation-related topics.
     */
    public void start() {
        try {

            // set a callback and connect to the broker
            mqttClient.setCallback(this);
            mqttClient.connect();

            //Subscribe to all subtopics of homestation
            final String topic = "iot_data/#";
            mqttClient.subscribe(topic);

            System.out.println("The subscriber is now listening to " + topic + "...");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
		System.out.println("Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // what happens when a new message arrive: in this case, we print it out.
		float temp = Float.parseFloat(message.toString());
		buffer.add(temp);
		System.out.println("-------------------------------------------------");
		System.out.println("| Topic:" + topic);
		System.out.println("| Message: " + new String(message.getPayload()));
    	System.out.println("| Temperature: " + temp);
		System.out.println("-------------------------------------------------");
    	
		for (float aux : buffer) {
			System.out.println(aux);
		}
		
        // additional action for the Last Will and Testament message
        if ("iot_data".equals(topic)) {
            System.err.println("Publisher is gone!");
        }

    }

    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // called when delivery for a message has been completed, and all acknowledgments have been received
        // no-op, here
    }
    
    /**
     * The main
     */
    public static void main(String[] args) {
        MQTTSubscriber subscriber = new MQTTSubscriber();
        subscriber.start();
    }
}