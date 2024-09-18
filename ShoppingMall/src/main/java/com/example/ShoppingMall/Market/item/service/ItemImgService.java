package com.example.ShoppingMall.Market.item.service;

import com.example.ShoppingMall.Market.item.entity.ItemImg;
import com.example.ShoppingMall.Market.item.repo.ItemImgRepository;
import com.example.ShoppingMall.exception.EntityNotFoundException;
import com.example.ShoppingMall.service.FileService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
   private final String itemImgLocation = "media/itemImg/";

   private final ItemImgRepository itemImgRepository;
   private final FileService fileService;

   private void checkPath(Path path) throws Exception {
       while (Files.notExists(path.getParent())) {
           checkPath(path.getParent());
       }
       if (Files.notExists(path)) {
           Files.createDirectory(path);
       }
   }
   public void saveItemImg(
           ItemImg itemImg,
           MultipartFile itemImgFile
   ) throws Exception {
       // Create empty variables to store image details
        //Get original img name
       String oriImgName = itemImgFile.getOriginalFilename();
       String imgName = "";
       String imgUrl = "";

       //File upload
       if(!StringUtils.isEmpty(oriImgName)){
           checkPath(Path.of(itemImgLocation));

           // Save the image file and return its saved name
           imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());

           // Set the URL where the image can be accessed after upload
           imgUrl = "/static/item/" + imgName;
       }

       // Save the item image information in the database
            // Update the image entity with new information
       itemImg.updateItemImg(oriImgName, imgName, imgUrl);
            // Save the updated entity into the repository
       itemImgRepository.save(itemImg);
        /*
        imgName: The actual name of the file saved on the server
        oriImgName: The original name of the file uploaded by the user
        imgUrl: The URL to access the uploaded file
        */
   }

    // Method to update the item image
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        if(!itemImgFile.isEmpty()){ // If the image file is provided for update
            // Retrieve the existing image entity from the database using the image ID
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(() -> new EntityNotFoundException("Item image not found"));

            checkPath(Path.of(itemImgLocation));
            // Delete the old image file if it exists
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+
                        savedItemImg.getImgName());
            }
            // Upload the new image file and get its name
            // Get the original name of the new file
            String oriImgName = itemImgFile.getOriginalFilename();
            //Upload the new file
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // 업데이트한 상품 이미지 파일을 업로드

            // Update the image entity with the new image details
            String imgUrl = "/static/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            itemImgRepository.save(savedItemImg);

             /*
        Why we don't use itemImgRepository.save(itemImg) again in the update method?
        Since `savedItemImg` is currently in a **persistent state**, Hibernate's dirty checking feature will automatically detect the changes to the entity.
        This means when the transaction ends, an update query will be automatically triggered.
        The important thing is that the entity is in a persistent state, so we don't have to manually call `save`.
        */
        }

   }
    public void deleteItemImg(Long itemImgId) throws Exception {
        ItemImg itemImg = itemImgRepository.findById(itemImgId)
                .orElseThrow(() -> new EntityNotFoundException("Item image not found"));

        String imgName = itemImg.getImgName();
        if (!StringUtils.isEmpty(imgName)) {
            fileService.deleteFile(itemImgLocation + "/" + imgName);
        }

        itemImgRepository.deleteById(itemImgId);
    }
}
