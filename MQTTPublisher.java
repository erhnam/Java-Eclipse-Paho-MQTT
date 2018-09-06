package mqtt;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

//Publish message
public class MQTTPublisher implements MqttCallback{
    
	// the broker URL
    private static final String BROKER_URL = "tcp://localhost:1883";
    // the (only) MQTT topic of this example
    private static final String TOPIC_TEMPERATURE = "iot_data/temperature";
    
    // init the client
	MqttClient client;
	  
	  public MQTTPublisher() {
	        // A randomly generated client identifier based on the user's login
	        // name and the system time
	        String clientId = MqttClient.generateClientId();

	        try {
	            client = new MqttClient(BROKER_URL, clientId);

	        } catch (MqttException e) {
	            e.printStackTrace();
	        }
	  }
	  
    /**
     * The method to start the publisher. Currently, it sets a Last Will and Testament
     * message, open a non persistent connection, and publish a temperature value
     */
    public void start() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            // persistent, durable connection
            options.setCleanSession(true);
            options.setKeepAliveInterval(30);
            options.setCleanSession(false);
            options.setWill(client.getTopic("iot_data"), "I'm gone. Bye.".getBytes(), 0, false);

            // connect the publisher to the broker
            client.connect(options);

            // publish something...
            publishTemperature();
            
            client.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * It prepares and publish the temperature value to a specific topic (/homestation/temperature).
     * @throws MqttException
     */
    private void publishTemperature() throws MqttException {
        // get the topic
        MqttTopic temperatureTopic = client.getTopic(TOPIC_TEMPERATURE);

        // message content
        String temperature = Double.toString(getRandomDoubleBetweenRange(0,50));

        // publish the message on the given topic
        // by default, the QoS is 1 and the message is not retained
        temperatureTopic.publish(new MqttMessage(temperature.getBytes()));

        // debug
        System.out.println("Published message on topic '" + temperatureTopic.getName() + "': " + temperature);
        
    }
	
    
    public static double getRandomDoubleBetweenRange(double min, double max){
        double x = (Math.random()*((max-min)+1))+min;
        return x;
    }
    
    @Override
    public void connectionLost(Throwable cause) {
    	System.out.println("Connection LOST: " + cause);   
    }
	
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
    	System.out.println(message);   
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    	//System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
    }
    
	/**
	 * The main
	 */
	public static void main(String[] args) {
	    MQTTPublisher publisher = new MQTTPublisher();
	    publisher.start();
	}    
}

