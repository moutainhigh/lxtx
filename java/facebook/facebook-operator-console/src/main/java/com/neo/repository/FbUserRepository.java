package com.neo.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neo.entity.FbUser;

public interface FbUserRepository extends JpaRepository<FbUser, BigInteger>{

}
