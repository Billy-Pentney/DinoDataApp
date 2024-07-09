package com.bp.dinodata

import com.bp.dinodata.data.ImageUrlData
import junit.framework.TestCase.assertEquals
import org.junit.Test




class ImageParsingUnitTest {

    companion object {

        const val test_data = "{\n" +
                "  \"Triceratops horridus\": [\n" +
                "    {\n" +
                "      \"id\": \"48f55012-d1a8-4083-a46e-9a3633e81c88\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"1536x646\",\n" +
                "        \"1024x431\",\n" +
                "        \"512x215\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"f2a8724b-4619-4dc2-a545-bea4412867f7\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"7035x3055\",\n" +
                "        \"1536x667\",\n" +
                "        \"1024x445\",\n" +
                "        \"512x222\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"8df79034-98a5-47aa-aa8f-27c725743a63\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"2137x818\",\n" +
                "        \"1536x588\",\n" +
                "        \"1024x392\",\n" +
                "        \"512x196\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"d36ccf73-05b9-4b16-83a1-cfe231ef7166\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"1010x412\",\n" +
                "        \"512x209\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Triceratops\": [\n" +
                "    {\n" +
                "      \"id\": \"4c3f2e42-c7c6-42d6-934d-fed47b6888a1\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"1536x763\",\n" +
                "        \"1024x509\",\n" +
                "        \"512x254\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Triceratops prorsus\": [\n" +
                "    {\n" +
                "      \"id\": \"4d63d730-10b0-4f69-b7c9-390fad409e7b\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"4628x2026\",\n" +
                "        \"1536x672\",\n" +
                "        \"1024x448\",\n" +
                "        \"512x224\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"0388ee19-b40e-46fd-92f5-8ef6b9075590\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"4643x1742\",\n" +
                "        \"1536x576\",\n" +
                "        \"1024x384\",\n" +
                "        \"512x192\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"c85d760c-f4ec-466b-906f-e9f43e7a1406\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"4801x2051\",\n" +
                "        \"1536x656\",\n" +
                "        \"1024x437\",\n" +
                "        \"512x219\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"1ef8f3a2-6c8b-400e-ab01-cc26ed9d1a4d\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"4802x2017\",\n" +
                "        \"1536x645\",\n" +
                "        \"1024x430\",\n" +
                "        \"512x215\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"aeeb30a8-afdc-4e7e-9bcc-574cb290a1f6\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"5452x2042\",\n" +
                "        \"1536x575\",\n" +
                "        \"1024x384\",\n" +
                "        \"512x192\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"9146defb-cd90-4136-af96-7021890d8ca7\",\n" +
                "      \"img_sizes\": [\n" +
                "        \"1536x672\",\n" +
                "        \"1024x448\",\n" +
                "        \"512x224\"\n" +
                "      ],\n" +
                "      \"thumb_sizes\": [\n" +
                "        \"192x192\",\n" +
                "        \"128x128\",\n" +
                "        \"64x64\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
    }



    @Test
    fun singleImageDataConvertedFromMap() {

        val id = "b7879e57-baca-4cd2-83c5-e85e8971a85f"

        val singleImageRawMap = mapOf(
            "id" to id,
            "img_sizes" to listOf("5000x2000", "1500x1990"),
            "thumb_sizes" to listOf("200x200", "50x50")
        )

        val result = ImageUrlData.fromMap(singleImageRawMap)

//        assertEquals("b7879e57-baca-4cd2-83c5-e85e8971a85f", result?.id)
//        assertEquals("5000x2000", result?.imageSizes?.get(0))
//        assertEquals("50x50", result?.thumbSizes?.last())

        val firstImageUrl = result?.getImageUrl(0)
        val expectedImageUrl = "https://images.phylopic.org/images/b7879e57-baca-4cd2-83c5-e85e8971a85f/raster/5000x2000.png"
        assertEquals(expectedImageUrl, firstImageUrl)
    }

}