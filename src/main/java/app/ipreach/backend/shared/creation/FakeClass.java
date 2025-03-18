package app.ipreach.backend.shared.creation;

import app.ipreach.backend.locations.payload.dto.LocationDto;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.EShiftUserRole;
import app.ipreach.backend.shared.enums.EStatus;
import app.ipreach.backend.shifts.payload.dto.ShiftAssignmentDto;
import app.ipreach.backend.shifts.payload.dto.ShiftDto;
import app.ipreach.backend.shifts.payload.dto.ShiftRequestDto;
import app.ipreach.backend.users.payload.dto.UserDto;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static app.ipreach.backend.shared.constants.DateTimePatterns.MONROVIA;
import static app.ipreach.backend.shared.creation.Generator.getRandomEnum;
import static app.ipreach.backend.shared.creation.Generator.getRandomListFromEnum;

public class FakeClass {

    private static final Faker faker = new Faker();

    public static UserDto giveMeUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String phoneNumber = faker.phoneNumber().cellPhone();

        return UserDto.builder()
            .id(RandomUtils.secure().randomLong())
            .email(faker.internet().emailAddress())

            .botId(RandomUtils.secure().randomLong())
            .botName(firstName)
            .botPhone(phoneNumber)

            .name(firstName + " " + lastName)
            .phone(phoneNumber)
            .phone2(faker.phoneNumber().cellPhone())

            .congregation(faker.planet().name())

            .roles(getRandomListFromEnum(ERole.class, 3))

            .approved(RandomUtils.secure().randomBoolean())

            .build();

    }

    public static LocationDto giveMeLocation() {
        return LocationDto.builder()
            .id(RandomUtils.secure().randomLong())
            .name(faker.address().cityName())
            .address(faker.address().fullAddress())
            .url(faker.internet().url())
            .details(faker.lorem().sentence(20))
            .build();
    }

    public static ShiftDto giveMeShift() {
        return giveMeShift(RandomUtils.secure().randomLong());
    }

    public static ShiftDto giveMeShift(long shiftId) {
        var dateTimeFrom = LocalDateTime.ofInstant(faker.timeAndDate().future(5, 1, TimeUnit.DAYS), MONROVIA);
        var dateTimeTo = dateTimeFrom.plusHours(2);
        var slots = RandomUtils.secure().randomInt(2, 6);

        return ShiftDto.builder()
            .id(shiftId)
            .location(giveMeLocation())
            .slotsOpened(slots)
            .slotsAvailable(RandomUtils.secure().randomInt(2, slots))
            .dateTimeFrom(dateTimeFrom)
            .dateTimeTo(dateTimeTo)
            .build();
    }

    public static ShiftDto giveMeShiftWithAssignment() {
        return giveMeShift().toBuilder()
            .assignments(giveMeAssignment(null))
            .build();
    }

    public static ShiftDto giveMeShiftWithAssignment(long shiftId) {
        ShiftDto shiftDto = giveMeShift(shiftId);

        return shiftDto.toBuilder()
            .assignments(giveMeAssignment(shiftId))
            .build();
    }

    public static ShiftAssignmentDto giveMeAssignment(Long shiftId) {
        List<UserDto> participants = new ArrayList<>();

        for (int i = 0; i < RandomUtils.secure().randomInt(2, 5); i++) {
            switch (i) {
                case 0 -> participants.add(giveMeUserWithRole(EShiftUserRole.OVERSEER));
                case 1 -> participants.add(giveMeUserWithRole(EShiftUserRole.AUXILIAR));
                default -> participants.add(giveMeUserWithRole(EShiftUserRole.PARTICIPANT));
            }
        }

        return ShiftAssignmentDto.builder()
            .id(RandomUtils.secure().randomLong())
            .shiftId(Optional.ofNullable(shiftId).orElse(RandomUtils.secure().randomLong()))
            .participants(participants)
            .build();
    }

    private static UserDto giveMeUserWithRole(EShiftUserRole shiftUserRole) {
        return giveMeUser().toBuilder().shiftUserRole(shiftUserRole).build();
    }

    public static ShiftRequestDto giveMeShiftRequest() {
        var shift = giveMeShift();

        return ShiftRequestDto.builder()
            .id(RandomUtils.secure().randomLong())
            .location(giveMeLocation())
            .user(giveMeUser())
            .shift(giveMeShift())
            .slotsRequested(RandomUtils.secure().randomInt(2, shift.slotsAvailable()))
            .status(getRandomEnum(EStatus.class))
            .build();
    }

}
