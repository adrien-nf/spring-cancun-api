package com.cancun.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
class ApiApplicationTests {
    @Autowired
    private MockMvc mockMvc;

	@Test
	public void contextLoads() {
		
	}
	
	@Test
	void testReservationIsMaximumThreeDaysLong() {
		//
	}

	@Test
	void testReservationIsMaximumThirtyDaysInAdvance() {
		//
	}

	@Test
	void testReservationStartsNextDayOfBooking() {
		//
	}

	@Test
	void testUserCanCheckRoomAvailability() throws Exception {
		mockMvc.perform(get("/api/rooms/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(1)));
	}
	
	@Test
	void testUserCanPlaceReservation() {
		//
	}

	@Test
	void testUserCanCancelReservation() {
		//
	}
	
	@Test
	void testUserCanUpdateReservation() {
		//
	}
}
