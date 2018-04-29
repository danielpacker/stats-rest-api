package org.danielpacker.restapi;

import org.danielpacker.restapi.service.Statistics;
import org.danielpacker.restapi.service.StatisticsTicker;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@WebMvcTest(RESTController.class)
@EnableScheduling
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RESTControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void test1Defaults() throws Exception {

        // Check that all defaults are correct without any trans submitted.
        mvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.sum", is(0.0)))
                .andExpect(jsonPath("$.max", is(0.0)))
                .andExpect(jsonPath("$.avg", is(0.0)))
                .andExpect(jsonPath("$.min", is(0.0)))
                .andExpect(jsonPath("$.max", is(0.0)));
    }

    @Test
    public void test2SingleTran() throws Exception {

        Statistics.clear();

        long time = System.currentTimeMillis() - 1500;
        String tranContent = "{\"amount\":100.00,\"timestamp\":"+ time + "}";

        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent).contentType(MediaType.APPLICATION_JSON_VALUE));

        // Simulate a time tick.
        StatisticsTicker ticker = new StatisticsTicker();
        ticker.doTick();

        // Check that the one tran shows up in stats.
        mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.sum", is(100.0)))
                .andExpect(jsonPath("$.max", is(100.0)))
                .andExpect(jsonPath("$.avg", is(100.0)))
                .andExpect(jsonPath("$.min", is(100.0)))
                .andExpect(jsonPath("$.max", is(100.0)));
    }

    @Test
    public void test3MultiTran() throws Exception {

        Statistics.clear();

        long time1 = System.currentTimeMillis()-1000;
        String tranContent1 = "{\"amount\":100.00,\"timestamp\":"+ time1 + "}";

        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent1).contentType(MediaType.APPLICATION_JSON_VALUE));

        long time2 = System.currentTimeMillis()-2000;
        String tranContent2 = "{\"amount\":200.00,\"timestamp\":"+ time2 + "}";

        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent2).contentType(MediaType.APPLICATION_JSON_VALUE));

        long time3= System.currentTimeMillis()-3000;
        String tranContent3 = "{\"amount\":300.00,\"timestamp\":"+ time3 + "}";


        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent3).contentType(MediaType.APPLICATION_JSON_VALUE));

        // Simulate a time tick.
        StatisticsTicker ticker = new StatisticsTicker();
        ticker.doTick();

        // Check aggregate stats for multiple transactions.
        mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.sum", is(600.0)))
                .andExpect(jsonPath("$.avg", is(200.0)))
                .andExpect(jsonPath("$.min", is(100.0)))
                .andExpect(jsonPath("$.max", is(300.0)));
    }

    @Test
    public void test4SingleTranFuture() throws Exception {

        long futureTime = System.currentTimeMillis() + 60*1000*60; // 60 mins ahead
        String tranContent = "{\"amount\":100.00,\"timestamp\":" + futureTime + "}";

        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test5SingleTranPast() throws Exception {

        long pastTime = System.currentTimeMillis() - 60*1000*60; // 60 mins ago
        String tranContent = "{\"amount\":100.00,\"timestamp\":" + pastTime + "}";

        mvc.perform(post("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(tranContent).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

}