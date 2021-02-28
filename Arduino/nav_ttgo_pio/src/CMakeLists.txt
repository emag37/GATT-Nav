# This file was automatically generated for projects
# without default 'CMakeLists.txt' file.

FILE(GLOB_RECURSE app_sources ${CMAKE_SOURCE_DIR}/src/*.*)

idf_component_register(SRCS ${app_sources})

set(ULP_APP_NAME "ulp_main")
set(ulp_s_sources ../ulp/ulp_main.S)
set(ulp_exp_dep_srcs "main.cpp")

ulp_embed_binary(${ULP_APP_NAME} "${ulp_s_sources}" "${ulp_exp_dep_srcs}")