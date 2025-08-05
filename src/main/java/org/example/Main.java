package org.example;

import org.example.service.WalletService;
import org.example.service.impl.WalletServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

}



// hot fix 1 and 2
// expert operation (submit -> all validation) -----> DONE
// valid status -> count proposal -> status order (check double)
// all Customer operation


// branch master & dev (dev for coding) -----> DONE


// phase-3
// filter & search for admin -----> DONE
// upload image error (non required)
// separated upload image
// image upload change status
// all expert feature
// history order expert -> pagination
// history rate single -> findByID 1/3 score
// see score any order
// delete from wallet customer
// transfer 70% to expert wallet -----> DONE
// link payment
// page payment
// 10 minutes time backend
// captcha backend -> validation in backend
// rate to order (disable account - negative rate) 1 to 5
// text rate (non required)


// phase4
// manage authorize preauthorize
// change JWT
// change expert status after email verify, upload image
// show wallet
// email just one send (find verify account) -----> DONE
// exception handler -----> DONE
// unit test