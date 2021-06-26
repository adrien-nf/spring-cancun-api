package com.cancun.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.cancun.api.model.Reservation;
import com.cancun.api.model.Room;
import com.cancun.api.repository.ReservationRepository;
import com.cancun.api.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Calendar;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class ApiApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeAll
    public void initAll() {
    	System.out.println("Starting tests...");
    }
    
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
		// Calendar's dates
		Calendar tomorrowDate = Calendar.getInstance(); 
		tomorrowDate.add(Calendar.DATE, 1);
		Calendar overmorrowDate = Calendar.getInstance();
		overmorrowDate.add(Calendar.DATE, 2);
		
		Reservation reservationToPost = new Reservation();
		
		System.out.println(asJsonString(reservationToPost));
		
		MvcResult result = mockMvc.perform(
				post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(asJsonString(reservationToPost)))
		.andExpect(status().isOk()).andReturn();

		Reservation returnedReservation = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Reservation.class);
		
		Assertions.assertEquals(reservationToPost.getStartDate(), returnedReservation.getStartDate());
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
