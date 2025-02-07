package app.ipreach.backend.shared.creation;

import app.ipreach.backend.dto.user.LocationDto;
import app.ipreach.backend.dto.user.ShiftDto;
import app.ipreach.backend.dto.user.ShiftRequestDto;
import app.ipreach.backend.dto.user.UserDto;
import app.ipreach.backend.shared.constants.ERole;
import app.ipreach.backend.shared.constants.EStatus;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
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
            .username(faker.internet().username())
            .email(faker.internet().emailAddress())

            .botId(RandomUtils.secure().randomLong())
            .botName(firstName)
            .botPhone(phoneNumber)

            .name(firstName + " " + lastName)
            .phone(phoneNumber)
            .phone2(faker.phoneNumber().cellPhone())

            .congregation(faker.planet().name())

            .userRoles(getRandomListFromEnum(ERole.class, 3))

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
