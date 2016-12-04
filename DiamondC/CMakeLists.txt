cmake_minimum_required(VERSION 3.6)
project(DiamondC)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    diamond.c
    diamond.h
    game.c
    Makefile)

add_executable(DiamondC ${SOURCE_FILES})