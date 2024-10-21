package com.webapp.library__app.controller;

import com.webapp.library__app.requestmodels.ReviewRequest;
import com.webapp.library__app.service.ReviewService;
import com.webapp.library__app.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:5174")
@RestController
@RequestMapping("/spi/reviews")
public class ReviewController {

        private ReviewService reviewService;

        public ReviewController(ReviewService reviewService){
            this.reviewService=reviewService;
        }

        @GetMapping("/secure/user/book")
        public Boolean reviewuserByUser(@RequestHeader(value="Authorization") String token,
                                        @RequestParam Long bookId) throws Exception{
            String userEmail=ExtractJWT.payloadJWTExtraction(token,"\"sub\"");
            if(userEmail==null){
                throw new Error("user email is missing");
            }
            return reviewService.userReviewListed(userEmail,bookId);
        }


        @PostMapping("/secure")
        public void postReview(@RequestHeader(value="Authorization") String token,
                               @RequestBody ReviewRequest reviewRequest) throws Exception{

            String userEmail= ExtractJWT.payloadJWTExtraction(token,"\"sub\"");
            if(userEmail==null){
                throw new Error("User EMail ALready Exist");
            }
             reviewService.postReview(userEmail, reviewRequest);
        }


}
