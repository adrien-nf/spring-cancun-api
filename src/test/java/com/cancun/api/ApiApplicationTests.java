package com.cancun.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

import com.cancun.api.config.H2JpaConfig;
import com.cancun.api.model.Reservation;
import com.cancun.api.model.Room;
import com.cancun.api.repository.ReservationRepository;
import com.cancun.api.repository.RoomRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = {ApiApplication.class, H2JpaConfig.class})
class ApiApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	
	private Room room;
	
	@BeforeEach
	public void initEach() {
		System.out.println("Starting tests...");

		LocalDate dateToday = LocalDate.now();
		
		// Create a room
		Room newRoom = new Room();
		newRoom.setName("Suite Deluxe");
		room = roomRepository.save(newRoom);

		// Add 4 reservations to this room
		ArrayList<Reservation> setupReservations = new ArrayList();
		setupReservations.add(new Reservation(room, dateToday.plusDays(1), dateToday.plusDays(2)));
		setupReservations.add(new Reservation(room, dateToday.plusDays(3), dateToday.plusDays(5)));
		setupReservations.add(new Reservation(room, dateToday.plusDays(8), dateToday.plusDays(10)));
		setupReservations.add(new Reservation(room, dateToday.plusDays(13), dateToday.plusDays(13)));

		setupReservations.forEach(reservation -> {
			room.book(reservation);
			reservationRepository.save(reservation);    		
		});
	}
	
	@AfterEach
	public void cleanEach() {
		reservationRepository.deleteAll();
		roomRepository.deleteAll();
	}
	
	@Test
	void testReservationIsMaximumThreeDaysLong() throws Exception {
		String dateInOneDay = LocalDate.now().plusDays(1).toString();
		String dateInTenDays = LocalDate.now().plusDays(10).toString();

		mockMvc.perform(
				post("/api/rooms/"+room.getId()+"/reservations")
				.param("start_date", dateInOneDay)
				.param("end_date", dateInTenDays))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testReservationIsMaximumThirtyDaysInAdvance() throws Exception {
		String dateInSixtyDays = LocalDate.now().plusDays(60).toString();
		String dateInSixtyOneDays = LocalDate.now().plusDays(61).toString();

		mockMvc.perform(
				post("/api/rooms/"+room.getId()+"/reservations")
				.param("start_date", dateInSixtyDays)
				.param("end_date", dateInSixtyOneDays))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testReservationIsMinimumTomorrow() throws Exception {
		String dateToday = LocalDate.now().toString();
		String dateInOneDay = LocalDate.now().plusDays(1).toString();
		
		mockMvc.perform(
				post("/api/rooms/"+room.getId()+"/reservations")
				.param("start_date", dateToday)
				.param("end_date", dateInOneDay))
		.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void testStartDateIsBeforeEndDate() throws Exception {
		String dateInOneDay = LocalDate.now().plusDays(1).toString();
		String dateInTwoDays = LocalDate.now().plusDays(2).toString();
		
		mockMvc.perform(
				post("/api/rooms/"+room.getId()+"/reservations")
				.param("start_date", dateInTwoDays)
				.param("end_date", dateInOneDay))
		.andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	void testUserCanPlaceReservation() throws Exception {
		String dateInFifteenDays = LocalDate.now().plusDays(15).toString();
		String dateInSeventeenDays = LocalDate.now().plusDays(17).toString();
		
		mockMvc.perform(
				post("/api/rooms/"+room.getId()+"/reservations")
				.param("start_date", dateInFifteenDays)
				.param("end_date", dateInSeventeenDays)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.reservations", hasSize(room.getReservations().size() + 1)));
	}
	
	@Test
	void testUserCanCheckRoomAvailability() throws Exception {
		// Room already booked
		// Get a random reservation
		Reservation reservationAlreadyBooked = room.getReservations().get((new Random()).nextInt(room.getReservations().size()));

		mockMvc.perform(get("/api/rooms/"+room.getId()+"/is-available")
				.param("start_date", reservationAlreadyBooked.getStartDate().toString())
				.param("end_date", reservationAlreadyBooked.getEndDate().toString())
				)
		.andExpect(status().isOk())
		.andExpect(content().string("false"));
		
		// Room is available for booking
		String dateInTwentyDay = LocalDate.now().plusDays(20).toString();
		String dateInTwentyTwoDays = LocalDate.now().plusDays(22).toString();

		mockMvc.perform(get("/api/rooms/"+room.getId()+"/is-available")
				.param("start_date", dateInTwentyDay)
				.param("end_date", dateInTwentyTwoDays)
				)
		.andExpect(status().isOk())
		.andExpect(content().string("true"));
	}

	@Test
	void testUserCanCancelReservation() throws Exception {
		// Get a random reservation
		Reservation reservationToBeDeleted = room.getReservations().get((new Random()).nextInt(room.getReservations().size()));
		
		mockMvc.perform(delete("/api/rooms/"+room.getId()+"/reservations/"+reservationToBeDeleted.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.reservations", hasSize(room.getReservations().size() - 1)));
	}
	
	@Test
	void testUserCanUpdateReservation() throws Exception {
		// Get a random reservation
		Reservation reservationToBeUpdated = room.getReservations().get((new Random()).nextInt(room.getReservations().size()));
		
		String newStartDate = LocalDate.now().plusDays(25).toString();
		String newEndDate = LocalDate.now().plusDays(27).toString();
		
		mockMvc.perform(
				patch("/api/rooms/"+room.getId()+"/reservations/"+reservationToBeUpdated.getId())
				.param("start_date", newStartDate)
				.param("end_date", newEndDate)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.reservations", hasSize(room.getReservations().size())))
		.andExpect(content().string(containsString("\"startDate\":\""+newStartDate+"\"")))
		.andExpect(content().string(containsString("\"endDate\":\""+newEndDate+"\"")));
	}
}
