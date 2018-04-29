package org.danielpacker.restapi;

import org.danielpacker.restapi.service.Statistics;
import org.danielpacker.restapi.service.StatisticsView;
import org.danielpacker.restapi.service.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class RESTController {

    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity transactions(@RequestBody Map<String, Object> json) {

        double amt = (double)json.get("amount");
        long ts = (long)json.get("timestamp");

        // If transaction rejected, it was too old or in future.
        if (Statistics.addTran(new Transaction(amt, ts))) {
            return new ResponseEntity(HttpStatus.CREATED); // 201
        }
        else {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // 204
        }
    }

    @RequestMapping(path = "/statistics", method = RequestMethod.GET)
    public StatisticsView statistics() {

        // Returns a POJO with all the stats needed for JSON.
        return Statistics.getStatsView();
    }


}
