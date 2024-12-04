package com.main.service.interfac;

import com.main.DTO.LoginRequest;
import com.main.DTO.Response;
import com.main.entity.User;

public interface UserService {
	
	Response register(User user);
	Response login(LoginRequest loginRequest);
	Response getAllUsers();
	Response getUserBookingHistory(String userId);
	Response deleteUser(String userId);
	Response getUserById(String userId);
	Response getMyInfo(String email);

}
