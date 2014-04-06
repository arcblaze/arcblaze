package com.arcblaze.arccore.rest.guest;

import javax.ws.rs.BadRequestException;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.mail.sender.ContactUsMailSender;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the password send capabilities.
 */
public class ContactUsResourceTest {
	/**
	 * Test how the resource responds when the provided name value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullName() {
		final Config config = new Config();
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final ContactUsResource resource = new ContactUsResource();
		resource.send(config, timer, null, "email@somewhere.com", "HELP",
				"message");
	}

	/**
	 * Test how the resource responds when the provided email value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullEmail() {
		final Config config = new Config();
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final ContactUsResource resource = new ContactUsResource();
		resource.send(config, timer, "Name", null, "HELP", "message");
	}

	/**
	 * Test how the resource responds when the provided type value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullType() {
		final Config config = new Config();
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final ContactUsResource resource = new ContactUsResource();
		resource.send(config, timer, "Name", "email@somewhere.com", null,
				"message");
	}

	/**
	 * Test how the resource responds when the provided message value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullMessage() {
		final Config config = new Config();
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final ContactUsResource resource = new ContactUsResource();
		resource.send(config, timer, "Name", "email@somewhere.com", "HELP",
				null);
	}

	/**
	 * Test how the resource responds when the provided message value is null.
	 */
	@Test
	public void testValidSend() {
		final Config config = new Config();
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final ContactUsMailSender mockMailSender = Mockito
				.mock(ContactUsMailSender.class);

		final ContactUsResource resource = new ContactUsResource(mockMailSender);
		resource.send(config, timer, "Name", "email@somewhere.com", "HELP",
				"message");
	}
}
