package test.on.data01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import checking.CheckException;
import checking.CheckingEngine;
import checking.ExecutionStatus;
import checking.RuleGroup;
import model.books.YearBook;
import test.TestPath;

@RunWith(PowerMockRunner.class)
// @PrepareForTest( { Instant.class })
public class CheckEngineTest {

	@Test
	public void test() {
		try {

			LocalDateTime second_october_2018_at_12H00 = LocalDateTime.of(LocalDate.of(2018, Month.DECEMBER, 2),
					LocalTime.of(12, 0));
			Instant second_october_2018_at_12H00_instant = second_october_2018_at_12H00.toInstant(ZoneOffset.UTC);
			mockStatic(Instant.class);
			when(Instant.now()).thenReturn(second_october_2018_at_12H00_instant);

			// stub(method(Instant.class,
			// "now")).toReturn(second_october_2018_at_12H00.toInstant(ZoneOffset.UTC));

			YearBook yb = new YearBook(new File(TestPath.BASE_PATH + "data-01\\2018"));

			CheckingEngine ce = new CheckingEngine();
			ExecutionStatus status = ce.execute(yb);
			if (status.equals(ExecutionStatus.FAILED)) {
				for (RuleGroup cp : ce.getPhases()) {
					for (CheckException cpe : cp.getExceptions()) {
						System.out.println(cpe.getMessage());
					}
				}
			}
			assertEquals(ExecutionStatus.SUCCESS, status);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Got unexpected exception: " + e.toString());
		}
	}

}
