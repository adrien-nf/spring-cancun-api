package com.cancun.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

import com.cancun.api.model.Reservation;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	void testUserCanPlaceReservation() throws Exception {
		Reservation reservationToPost = new Reservation();
		Reservation reservationToReturn = new Reservation();
        
		mockMvc.perform(
				post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(asJsonString(reservationToPost)))
		.andExpect(status().isOk());
	}

	@Test
	void testUserCanCancelReservation() throws Exception {
		mockMvc.perform(delete("/api/reservations/1"))
		.andExpect(status().isOk());
	}
	
	@Test
	void testUserCanUpdateReservation() throws Exception {
		mockMvc.perform(put("/api/reservations/1"))
		.andExpect(status().isOk());
	}
	
	static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
