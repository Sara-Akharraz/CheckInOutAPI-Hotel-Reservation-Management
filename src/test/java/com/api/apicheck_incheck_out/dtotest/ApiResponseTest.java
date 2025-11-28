package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class ApiResponseTest {
    @Test
    void testApiResponse_Getters_Setters_ToString_HashCode_Equals(){
        ApiResponse<String> response1 = new ApiResponse<>(true, "Success", "data");

        assertTrue(response1.isSuccess());
        assertEquals("Success", response1.getMessage());
        assertEquals("data", response1.getData());

        response1.setSuccess(false);
        response1.setMessage("Error");
        response1.setData("data1");
        assertFalse(response1.isSuccess());
        assertEquals("Error", response1.getMessage());
        assertEquals("data1", response1.getData());

        assertNotNull(response1.toString());

        ApiResponse<String> response2 = new ApiResponse<>(false, "Error", "data1");
        ApiResponse<String> response3 = new ApiResponse<>(true, "Success", "data3");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());

    }
}
