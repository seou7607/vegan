package com.vegan.magazine.service;

import java.io.File;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vegan.magazine.dao.MagazineDAO;

import com.vegan.magazine.dto.MagazineDTO;



@Service
public class MagazineService {
   
Logger logger = LoggerFactory.getLogger(getClass());
   
   @Autowired MagazineDAO dao;
   
   /*
   public ArrayList<MagazineDTO> list() {
      return dao.list();
   }
   */

   public HashMap<String, Object> list(int page, int cnt) {
      logger.info(page+" 페이지 보여줘");
      logger.info("한 페이지에 "+cnt+" 개씩 보여줄거야");
      HashMap<String, Object> map = new HashMap<String, Object>();      
      
      // 1page = offset : 0
      // 2page = offset : 5
      // 3page = offset : 10
      int offset = (page-1)*cnt;
      
      // 만들 수 있는 총 페이지 수 
      // 전체 게시물 / 페이지당 보여줄 수
      int total = dao.totalCount();
      int range = total%cnt == 0 ? total/cnt : (total/cnt)+1;
      logger.info("전체 게시물 수 : "+total);
      logger.info("총 페이지 수 : "+range);
      
      page = page > range ? range : page;
      
      map.put("currPage", page);
      map.put("pages", range);
      
      ArrayList<MagazineDTO> list = dao.list(cnt, offset);
      map.put("list", list);
      logger.info("list : "+list);
      logger.info("map : "+map);
      return map;
   }
   
   
   public String magazinewrite(MultipartFile photo, HashMap<String, String> params) {
      
      String page = "redirect:/magazine";
      
      // 1. 게시글만 작성한 경우
      // 빙금 insert 한 값의 key 를 반환 받는 방법
      // 조건 1. 파라메터를 dto 로 보내야 한다.
      MagazineDTO dtoo = new MagazineDTO();
      //receipeDTO.setCat_id("recipe");
      // dto.setCat_id("mpd");
      //logger.info("loginId :"+loginId);
    
      dtoo.setCat_id(params.get("cat_id"));
      dtoo.setBoard_title(params.get("board_title"));
      dtoo.setUser_id(params.get("user_id"));
      dtoo.setBoard_content(params.get("board_content"));
      logger.info("cat_id : " + dtoo.getCat_id());
      int row = dao.magazinewrite(dtoo);
      logger.info("update row : "+row);
      
      // 조건 3. 받아온 키는 파라메터 dto 에서 뺀다.
      int board_id = dtoo.getBoard_id();
      String cat_id = dtoo.getCat_id();
      logger.info("방금 insert 한 board_id : "+board_id);
      
      page = "redirect:/magazineDetail.do?board_id="+board_id;
      
      // 2. 파일도 업로드 한 경우
      if(!photo.getOriginalFilename().equals("")) {
         logger.info("파일 업로드 작업");
         
         fileSave(cat_id,board_id,photo);
      }
      return page;
   }
   
   

   private void fileSave(String cat_id, int board_id, MultipartFile file) {
      // 1. 파일을 C:/img/upload/ 에 저장
      //1-1. 원본 이름 추출
      String ori_photo_name = file.getOriginalFilename();
      //1-2. 확장자 추출
      String ext = ori_photo_name.substring(ori_photo_name.lastIndexOf("."));
      //1-3. 새이름 생성 + 확장자
      String photo_name = System.currentTimeMillis()+ext;
      logger.info(ori_photo_name+" => "+photo_name);      
      try {
         byte[] bytes = file.getBytes();//1-4. 바이트 추출
         //1-5. 추출한 바이트 저장
         Path path = Paths.get("C:/img/upload/"+photo_name);
         Files.write(path, bytes);
         logger.info(photo_name+" save OK");
         // 2. 저장 정보를 DB 에 저장
         //2-1. 가져온 idx, oriFileName, newFileName insert
         dao.fileWrite(cat_id, board_id, ori_photo_name,photo_name);
                        
      } catch (IOException e) {
         e.printStackTrace();
      }
      
   }
   
   

   public MagazineDTO detail(String board_id, String flag) {
      if(flag.equals("detail")) {
         dao.up_views(board_id);
      }
      return dao.detail(board_id);
   }
   

   public void delete(String board_id) {
      // 1. photo 에 해당 board_id 값이 있는지?
      String photo_name = dao.findFile(board_id);
      logger.info("file name : "+photo_name);
      // 2. 없다면?
      int row = dao.delete(board_id);
      logger.info("delete data : "+row);
      
      if(photo_name != null && row>0) {
         File file = new File("C:/img/upload/"+photo_name);
         if(file.exists()) {
            file.delete();
         }
      }
   }

   public String update(MultipartFile photo, HashMap<String, String> params) {
      
      logger.info("idx : " + params.get("board_id"));
      int idx = Integer.parseInt(params.get("board_id"));
      int row = dao.update(params);
      // 아래 코드 한줄 대로 하면 false 일떄 true 로 바로 바꿔주기 때문에 삭제 버튼 눌렀을 때만 true 로 바껴야 하는데 
      // 그래서 그 바로 아래 코드로 수정함 그러면 삭제 버튼 눌렀을때만 수정되고 나머지는 다 false
      //boolean deletePhoto = params.get("deletePhoto") != null;
      boolean deletePhoto = "true".equals(params.get("deletePhoto"));
      logger.info("delete photo : "+ deletePhoto);
      String page = "redirect:/magazineDetail.do?board_id="+idx;
      logger.info("row:"+row);
      
      if (deletePhoto) {
          String photo_name = params.get("photo_name");
          logger.info("사진 삭제");
          logger.info("del serPhotoname: " +photo_name);
          int rowphoto = dao.photoDelete(photo_name);
          if(rowphoto == 1) {
             logger.info("사진 삭제 처리 완료");
             page = rowphoto > 0 ? "redirect:/magazineDetail.do?board_id="+idx : "redirect:/magazine";
             logger.info("update => "+page);
          }
      
      }
      
      if(row == 1) {
  
          logger.info("매거진 사진 수정 완료");
          

      // 2. photo 에 파일명이 존재 한다면?
      if(photo != null && !photo.getOriginalFilename().equals("")) {
    	 String cat_id = params.get("cat_id");
         fileSave(cat_id,idx, photo);
      }
     }

      return page;
      
   }


    public ArrayList<MagazineDTO> magacommentlist2(String board_id) {
         ArrayList<MagazineDTO> magacommentlist = dao.magacommentlist(board_id);
         return magacommentlist;
      }



    public int commwrite(String board_id, String comment_content, String loginId) {
         
         
        // MagazineDTO dto = new MagazineDTO();
         
       //dto.setCat_id("mc");
       //dto.setUser_id(params.get("user_id"));
       //dto.setComment_content(params.get("comment_content"));

         /*
          * int idx = dto.getBoard_id(); String cat_id = dto.getCat_id();
          * logger.info("방금 insert 한 comment 보드 아이디 : "+idx);
          * dao.commwrite(dto,board_id,params);
          */
         
         return dao.commwrite(board_id,comment_content,loginId);
      }


   


   public int commdelete(String board_id, String comment_id, String loginId) {
     
  
	return dao.commdelete(board_id,comment_id,loginId);
      
   }


   
   
   public MagazineDTO magacommentlist(String board_id, String comment_id, String comment_content) {
      return dao.magacommentlist2(board_id, comment_id, comment_content);
   }
   

   //public String commupdate(HashMap<String, String> params) {
      
         //int row = dao.commupdate(params);
         
        // logger.info("update row :"+row);
      
      //return "redirect:/magazineDetail.do?board_id="+params.get("board_id");
   //}

   
   
   public int commupdate(String comment_id, String comment_content, String loginId) {
      
      return dao.commupdate(comment_id,comment_content, loginId);
   }


	public byte adminChk(String loginId) {
		
		return dao.adminChk(loginId);
	}
   



}