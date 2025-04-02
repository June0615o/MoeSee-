package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.dto.RegisterDTO;

import java.util.List;

public interface RegisterService {
    RegisterDTO registerUser(List<Integer> clusterIds);
}
