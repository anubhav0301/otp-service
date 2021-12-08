package com.rapipay.otpproject.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.rapipay.otpproject.entity.Email;
import com.rapipay.otpproject.exception.InvalidEmailException;
import com.rapipay.otpproject.exception.InvalidOTPException;
import com.rapipay.otpproject.exception.OTPExpireException;
import com.rapipay.otpproject.exception.ResourseNotFoundException;
import com.rapipay.otpproject.dao.EmailDao;

@Service
public class ServicesImpl implements AllServices {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private EmailDao emailrepo;

	@Override
	@Async
	public List<Email> getAllEmail() {

		return emailrepo.findAll();
	}

	
	@Override
	@Async
	public Email getByEmail(String email) throws ResourseNotFoundException {

		return emailrepo.findById(email).orElseThrow(() -> new ResourseNotFoundException("Email not Found"));
	}

	
	@Override
	@Async
	public ResponseEntity<String> addEmail(Email email) throws InvalidEmailException {
		Email x = emailrepo.save(email);
		sendEmail(email.getEmail(), email.getOtp());
		if (x != null) {
			return new ResponseEntity<String>("OTP Sent",HttpStatus.OK);
		} else {
			throw new InvalidEmailException("Enter valid Email");
		}
	}

	
	void sendEmail(String email, int otp) {
		SimpleMailMessage messsage = new SimpleMailMessage();
		messsage.setTo(email);
		messsage.setSubject("OTP from RAPIPAY");
		messsage.setText("Enter OTP: " + otp + ". The OTP will expire in 10 minutes");
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(()->{
		javaMailSender.send(messsage);
		}).start();
	}
	
	
	@Override
	@Async
	public ResponseEntity<String>OtpValidate(Email eo)
			throws ResourseNotFoundException, OTPExpireException, InvalidOTPException {

		Email emailData = getByEmail(eo.getEmail());
		if (emailData == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			if (emailData.getOtp() == (eo.getOtp())) {
				LocalDateTime startTime = emailData.getGeneratedTime();
				LocalDateTime endTime = emailData.getExpiryTime();
				LocalDateTime currentTime = LocalDateTime.now();
				System.out.println(ChronoUnit.MINUTES.between(startTime, currentTime));
				System.out.println(ChronoUnit.MINUTES.between(currentTime, endTime));
				if (ChronoUnit.MINUTES.between(startTime, currentTime) <= 1
						&& ChronoUnit.MINUTES.between(currentTime, endTime) >= 0) {
					return new ResponseEntity<String>("Otp Validated",HttpStatus.OK);
				}
				throw new OTPExpireException("OTP Expired");
			}
			throw new InvalidOTPException("Invalid OTP");
		}
	}
}
