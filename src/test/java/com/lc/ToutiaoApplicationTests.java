package com.lc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.lc.ToutiaoApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@WebAppConfiguration
public class ToutiaoApplicationTests {

	@Test
	public void contextLoads() {
	}

}
