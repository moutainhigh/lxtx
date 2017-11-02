package com.neo.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neo.entity.Adset;

public interface AdsetRepository extends JpaRepository<Adset, BigInteger>{

}
