package com.example.ShoppingMall.Market.item.service;

import com.example.ShoppingMall.AuthenticationFacade;
import com.example.ShoppingMall.Market.item.dto.*;
import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.entity.ItemImg;
import com.example.ShoppingMall.Market.item.repo.ItemImgRepository;
import com.example.ShoppingMall.Market.item.repo.ItemRepository;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.Market.shop.repo.ShopRepository;
import com.example.ShoppingMall.user.entity.UserEntity;
import com.example.ShoppingMall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

// This service defines the methods create, read, update, delete item
public class ItemService {
    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final AuthenticationFacade facade;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

    //I - CREATE
    // Save item with images
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // Check if the user has permission to add items
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You do not have permission");
        // Find shop by shopId
        Optional<ShopEntity> optionalShop = shopRepository.findById(itemFormDto.getShopId());
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Shop not found with id: " + itemFormDto.getShopId());
        ShopEntity shop = optionalShop.get();
        // Check if the user is the owner of the shop
        if (!shop.getOwner().equals(currentUser))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to add items to this shop");

        // Create item
        ItemEntity item = itemFormDto.createItem(shop);

        //save item to generate itemId
        itemRepository.save(item);
        if (item.getId() == null)
            throw new Exception("Item ID is null after saving!");

        // Initialize the itemImgIds list in ItemFormDto
        List<Long> itemImgIds = new ArrayList<>();

        // save item image
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0)
                itemImg.setRepImgYn("Y"); // Set the first image as the representative product image
            else
                itemImg.setRepImgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i)); // Save the image

            // Add the ID of the saved image to the itemImgIds list
            itemImgIds.add(itemImg.getId());
        }
        // Set the itemImgIds in the DTO
        itemFormDto.setItemImgIds(itemImgIds);

        return item.getId();
    }

    //II - READ

    // Helper method to check if the user is the owner of the shop
    private boolean isShopOwner(UserEntity user, ItemEntity item) {
        ShopEntity shop = item.getShop();
        return shop != null && shop.getOwner().equals(user);
    }

    // Read one
    // Get item details for updating
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        UserEntity userEntity = facade.getCurrentUserEntity();

        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        // Check if the user is an admin or the owner of the shop
        if (userEntity.getRole().equals(UserRole.ROLE_ADMIN) || isShopOwner(userEntity, item)) {
            List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
            List<ItemImgDto> itemImgDtoList = itemImgList.stream()
                    .map(ItemImgDto::fromEntity)
                    .collect(Collectors.toList());

            ItemFormDto itemFormDto = ItemFormDto.fromEntity(item);
            itemFormDto.setItemImgDtoList(itemImgDtoList);
            return itemFormDto;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are not authorized to view this item");
        }
    }

    // Read All
    // Get item list for admin
    @Transactional(readOnly = true)
    public List<ItemEntity> getAdminItemList() {
        UserEntity userEntity = facade.getCurrentUserEntity();
        if (!userEntity.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You do not have permission");

        return itemRepository.findAll();
    }

    // Read item list by shopId
    //Buyer and shop owner can read item list buy shopId
    public List<ItemDto> getItemListByShopId(Long shopId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        if (currentUser.getRole().equals(UserRole.ROLE_INACTIVE))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You must update basic information to activate your account!");
        List<ItemEntity> items = itemRepository.findAllByShopId(shopId);
        return items.stream().map(ItemDto::fromEntity).collect(Collectors.toList());
    }

    //III - UPDATE
    // Update item data
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        UserEntity userEntity = facade.getCurrentUserEntity();

        // Check if the user has permission to update items
        if (!userEntity.getRole().equals(UserRole.ROLE_BUSINESS)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permission to update items");
        }

        // Find and update the item
        ItemEntity item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        // Check if the user is the owner of the shop
        if (!isShopOwner(userEntity, item)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this item");
        }

        if(itemFormDto.getName() != null) {
            item.setName(itemFormDto.getName());
        }
        if(itemFormDto.getDescription() != null) {
            item.setDescription(item.getDescription());
        }
        if(itemFormDto.getItemStatus() != null) {
            item.setItemStatus(itemFormDto.getItemStatus());
        }
        item.setPrice(itemFormDto.getPrice());
        item.setStock(itemFormDto.getStock());

        // item.updateItem(itemFormDto);
        itemRepository.save(item);

        // delete all itemImg with the id defined in itemFormDto
        for (ItemImg itemImg: itemImgRepository.findByItemId(item.getId())) {
            // delete image locally
            itemImgService.deleteItemImg(itemImg.getId());
            // delete image info in SQL database
            itemImgRepository.deleteById(itemImg.getId());
        }
        // Add images as update
        ItemImg itemImg = new ItemImg();
        itemImg.setItem(item);
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImg.setRepImgYn(i==0 ? "Y": "N"); // Set the first image as the representative product image
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    // DELETE ITEM
    public void deleteItem(Long itemId) throws Exception {
        UserEntity userEntity = facade.getCurrentUserEntity();

        // Find item by itemId
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        // Find shop and check owner
        ShopEntity shop = shopRepository.findById(item.getShop().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found"));

        if (!shop.getOwner().equals(userEntity)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission");
        }

        // Delete related item images (if any)
        List<ItemImg> itemImgs = itemImgRepository.findByItemId(itemId);
        for (ItemImg itemImg : itemImgs) {
            itemImgService.deleteItemImg(itemImg.getId());
        }

        // Delete item
        itemRepository.deleteById(itemId);
    }
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(
            Long shopId,
            String name,
            Integer minPrice,
            Integer maxPrice
    ) {
        List<ItemEntity> items = itemRepository.searchItems(shopId, name, minPrice, maxPrice);
        return items.stream().map(ItemDto::fromEntity).collect(Collectors.toList());
    }



}
