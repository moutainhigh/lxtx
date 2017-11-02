package com.neo.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neo.entity.FilterWords;

public interface FilterWordsRepository extends JpaRepository<FilterWords, BigInteger> {

}
