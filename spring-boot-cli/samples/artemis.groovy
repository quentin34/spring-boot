package org.test

@Grab("spring-boot-starter-artemis")
@Grab("artemis-jms-server")
import java.util.concurrent.CountDownLatch
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl

@Log
@Configuration
@EnableJms
class JmsExample implements CommandLineRunner {

	private CountDownLatch latch = new CountDownLatch(1)

	@Autowired
	JmsTemplate jmsTemplate

	void run(String... args) {
		def messageCreator = { session ->
			session.createObjectMessage("Greetings from Spring Boot via Artemis")
		} as MessageCreator
		log.info "Sending JMS message..."
		jmsTemplate.send("spring-boot", messageCreator)
		log.info "Send JMS message, waiting..."
		latch.await()
	}

	@JmsListener(destination = 'spring-boot')
	def receive(String message) {
		log.info "Received ${message}"
		latch.countDown()
	}

	@Bean JMSQueueConfigurationImpl springBootQueue() {
		JMSQueueConfigurationImpl impl = new  JMSQueueConfigurationImpl()
		impl.setName('spring-boot')
		impl.setDurable(false)
	}

}
