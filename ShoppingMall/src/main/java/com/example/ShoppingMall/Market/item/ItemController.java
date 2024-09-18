package com.example.ShoppingMall.Market.item;


import com.example.ShoppingMall.Market.item.dto.ItemDto;
import com.example.ShoppingMall.Market.item.dto.ItemFormDto;
import com.example.ShoppingMall.Market.item.dto.ItemSearchDto;
import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //CREATE
    @PostMapping("/add")
    public ResponseEntity<Long> createItem(
            @RequestParam("itemFormDto") String itemFormDtoJson,
            @RequestParam("itemImgFileList") List<MultipartFile> itemImgFileList
    )
            throws Exception
    {
        // Convert JSON to ItemFormDto
        ObjectMapper objectMapper = new ObjectMapper();
        ItemFormDto itemFormDto = objectMapper.readValue(itemFormDtoJson, ItemFormDto.class);

        Long itemId = itemService.saveItem(itemFormDto, itemImgFileList);
        return new ResponseEntity<>(itemId, HttpStatus.CREATED);
    }
    //READ
    // Get item details
    @GetMapping("/{itemId}/view")
    public ResponseEntity<ItemFormDto> getItem(@PathVariable Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        return ResponseEntity.ok(itemFormDto);
    }
    //read all for admin
    @GetMapping("/admin")
    public ResponseEntity<List<ItemEntity>> getAdminItemsList(ItemSearchDto itemSearchDto) {
        List<ItemEntity> items = itemService.getAdminItemList();
        return ResponseEntity.ok(items);
    }

    //read all by shopId
    @GetMapping("/{shopId}")
    public ResponseEntity<List<ItemDto>> readAllByShopId(@PathVariable Long shopId) {
        List<ItemDto> items = itemService.getItemListByShopId(shopId);
        return ResponseEntity.ok(items);
    }

    //UPDATE
    @PutMapping("/{itemId}/update")
    public ResponseEntity<Long> updateItem(
            @PathVariable Long itemId,
            @RequestParam("itemFormDto") String itemFormDtoJson,
            @RequestParam("itemImgFileList") List<MultipartFile> itemImgFileList
    ) throws Exception {

        // Convert JSON to ItemFormDto
        ObjectMapper objectMapper = new ObjectMapper();
        ItemFormDto itemFormDto = objectMapper.readValue(itemFormDtoJson, ItemFormDto.class);

        // Set the ID in the DTO
        itemFormDto.setId(itemId);
        Long updatedItemId = itemService.updateItem(itemFormDto, itemImgFileList);
        return ResponseEntity.ok(updatedItemId);
    }

    // DELETE
    @DeleteMapping("/{itemId}/delete")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) throws Exception {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

}
