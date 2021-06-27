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

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
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
	void testReservationIsMaximumThreeDaysLong() throws Exception {
		LocalDate dateInOneDay = LocalDate.now().plusDays(1);
		LocalDate dateInTenDays = LocalDate.now().plusDays(10);

		mockMvc.perform(
				post("/api/rooms/1/book")
				.param("start_date", dateInOneDay.toString())
				.param("end_date", dateInTenDays.toString()))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testReservationIsMaximumThirtyDaysInAdvance() throws Exception {
		LocalDate dateInSixtyDays = LocalDate.now().plusDays(60);
		LocalDate dateInSixtyOneDays = LocalDate.now().plusDays(61);

		mockMvc.perform(
				post("/api/rooms/1/book")
				.param("start_date", dateInSixtyDays.toString())
				.param("end_date", dateInSixtyOneDays.toString()))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testReservationIsMinimumTomorrow() throws Exception {
		LocalDate dateToday = LocalDate.now();
		LocalDate dateInOneDay = LocalDate.now().plusDays(1);
		
		mockMvc.perform(
				post("/api/rooms/1/book")
				.param("start_date", dateToday.toString())
				.param("end_date", dateInOneDay.toString()))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testStartDateIsBeforeEndDate() throws Exception {
		LocalDate dateInOneDay = LocalDate.now().plusDays(1);
		LocalDate dateInTwoDays = LocalDate.now().plusDays(2);
		
		mockMvc.perform(
				post("/api/rooms/1/book")
				.param("start_date", dateInTwoDays.toString())
				.param("end_date", dateInOneDay.toString()))
		.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	void testUserCanPlaceReservation() throws Exception {
		// Calendar's dates
		Calendar tomorrowDate = Calendar.getInstance(); 
		tomorrowDate.add(Calendar.DATE, 1);
		Calendar overmorrowDate = Calendar.getInstance();
		overmorrowDate.add(Calendar.DATE, 2);
		
		Reservation reservationToPost = new Reservation();
		
		MvcResult result = mockMvc.perform(
				post("/api/reservations")
				.contentType(MediaType.APPLICATION_JSON)
		        .content(asJsonString(reservationToPost)))
		.andExpect(status().isOk()).andReturn();

		Reservation returnedReservation = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Reservation.class);
		
		Assertions.assertEquals(reservationToPost.getStartDate(), returnedReservation.getStartDate());
	}
	
	@Test
	void testUserCanCheckRoomAvailability() throws Exception {
		mockMvc.perform(get("/api/rooms/1/is-available"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void testUserCanCancelReservation() throws Exception {
		mockMvc.perform(delete("/api/rooms/1/reservations/1"))
		.andExpect(status().isOk());
	}
	
	@Test
	void testUserCanUpdateReservation() throws Exception {
		mockMvc.perform(put("/api/rooms/1/reservations/1"))
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
