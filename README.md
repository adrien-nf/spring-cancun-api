# Cancun Hotel API
## Context
This application was developed as part of a test. The goal was to develop an API for the last hotel of Cancun. You can request the API and get different informations.
## How to run
Please note that this API was built using Maven 3.8.1, and JDK 11.0.11.
- Clone this repository.
- Run ``mvn clean package`` inside of the newly created folder. This will create a .jar file in ``target/``.
- Run the jar file using the command ``java ./target/api-0.0.1-SNAPSHOT.jar``.

You may now access the API.
## API Documentation
API documentation is available on my [Stoplight Profile](https://adrien-nf.stoplight.io/docs/cancun-api).
## Informations
### Miscellaneous
Please note that application.properties keys are not encrypted, for simplicity sake. In an actual project, the file would be encrypted, and usually not on the GitHub.
### Database
The development was done under a MySQL 5.7.24 database.
### Testing
The tests are executed on a different database. Indeed, so as not to erase data from your environment, tests are done "in-memory". This means that, while they work, they will not have any repercussion on your database.
![image](https://user-images.githubusercontent.com/22148893/123548652-f3fc5080-d765-11eb-8f4f-3d1c4dba2101.png)

## Resolution of the subject
### API will be maintained by the hotel’s IT department.
This is easy to do, as the code is quite clear and clean. The documentation may help newcomers take their marks.
### As it’s the very last hotel, the quality of service must be 99.99 to 100% => no downtime
This may be easily achieved with some server-side load balancing. Usually, I would create two to three instances of the API, with three endpoints. This ensures that every request is resolved.
### For the purpose of the test, we assume the hotel has only one room available
The API may still be used to manage numerous rooms, but for the purposes of this project, it is assumed that only one room is available and thus, the requests are not redirect to an available room.
### To give a chance to everyone to book the room, the stay can’t be longer than 3 days and can’t be reserved more than 30 days in advance.
This has been handled in the controllers, as they are calling RoomService.areDatesCorrect:
![image](https://user-images.githubusercontent.com/22148893/123548004-5869e080-d763-11eb-81c2-a20b742cc126.png)

This function ensures numerous things:
- Duration is no more than 3 days.
- Duration is in the future (at least one day from today).
- Reservation starts no more than 30 days in advance (Note: this means that a reservation may end in 32 days, as long as it starts in 30 days).
- Start date is before end date.
### All reservations start at least the next day of booking,
See previous function: Duration is in the future (at least one day from today).
### To simplify the use case, a “DAY’ in the hotel room starts from 00:00 to 23:59:59.
This is tackled by the fact that I used LocalDate in this project, the days are therefore of the yyyy-MM-dd format.
### Every end-user can check the room availability, place a reservation, cancel it or modify it.
#### Availability
The availability is meant to be checked with the ``GET: /api/rooms/{roomId}/is-available`` endpoint. Two parameters are mandatory: start_date and end_date. The route returns true or false, depending on whether the room is available for these two dates or not.
Note that you may also check the complete room object with the ``GET: /api/rooms/{roomId}`` endpoint.
#### Place a reservation
This is done via the ``POST: /api/rooms/{roomId}/reservations`` endpoint. Two parameters are mandatory: start_date and end_date.
#### Cancel a reservation
This is done via the ``DELETE: /api/rooms/{roomId}/reservations/{reservationId}`` endpoint.
#### Update a reservation
This is done via the ``PATCH: /api/rooms/{roomId}/reservations/{reservationId}`` endpoint. Two parameters are mandatory: start_date and end_date.
### To simplify the API is insecure.
No authentication has been made for this API. You may simply request, and anyone may update or delete anyone else's reservations.
### Go even further
To go a bit further, we might simply add a message body to the errors returned in the controller. This might be an easy improvement to do. For simplicity sake, I did not add messages in this project, that would be very simple to achieve:
```
return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action.");
```
