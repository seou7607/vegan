<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>   
<style>
   .none {
      display: none !important;
   }
   .textDeco {
      text-decoration: line-through;
   }
   #list a {
      text-decoration-line: none;
   }
   .pagination {
      justify-content: center;
   }
</style>
</head>
<body>
<%@ include file="./header.jsp" %>
<div class="contentWrap mt-5">
	<div class="contentBox">
      <div class="text-center">
         <h2>자유게시판</h2>
      </div>
      <div>
         <div class="d-flex" style="float: left;">
            <span class="mt-1">게시물 갯수&nbsp;:&nbsp;</span>
            <select id="pagePerNum" class="form-select w-auto">
               <option value="5">5</option>
               <option value="10">10</option>
               <option value="15">15</option>
               <option value="20">20</option>
            </select>
         </div>
         <div style="float: right;" class="d-flex">
            <button class="btnCtrl btn btn-outline-primary float-right" onclick="location.href='freewrite.go'">글쓰기</button>
         </div>         
      </div>
	  <div style="margin-top: 70px;">
      <table class="table table-striped table-bordered">
         <thead>
            <tr>
               <th class="text-center col-md-1">번호</th>
               <th class="text-center" style="width:25%">제목</th>
               <th class="text-center col-md-1">작성자</th>
               <th class="text-center col-md-1">작성일</th>
               <th class="text-center col-md-1">조회수</th>
            </tr>
         </thead>
         <tbody id="list">
         </tbody>
      </table>
      </div>
      <div id="paging">
         <nav aria-label = "Page navigation">
            <ul class="pagination" id="pagination"></ul>
         </nav>
      </div>
	</div>
</div>
</body>
<script>
var loginId = '<%=(String)session.getAttribute("loginId")%>';
var showPage= 1;
buttonControl(loginId);
listCall(showPage);

$('#pagePerNum').change(function(){
   listCall(showPage);
   $('#pagination').twbsPagination('destroy');
});

$("#searchText").keydown(function (key) {
   if (key.keyCode == 13) {
      listCall(1);
      $('#pagination').twbsPagination('destroy');
   }
});

$("#search").click(function (key) {
   listCall(1);
   $('#pagination').twbsPagination('destroy');
});

function listCall(page){
	$.ajax({
		type: 'post',
		url: 'freeList.ajax',
		data: {
			'searchText' : $("#searchText").val(),
			'page': page,
			'cnt' : $('#pagePerNum').val(),
			'loginId': loginId
		},
		dataType: 'json',
		success: function(data){
			listPrint(data);
		},
		error: function(e){
			console.log(e);
		}
	});
}

function listPrint(data){
	console.log("listPrint!");
	var content="";
  	
	if(data.list.length > 0) {
		data.list.forEach(function(board,board_id){
			content +='<tr>';
			content +='<td class="text-center col-md-1">'+board.board_id+'</td>';
			content +='<td class="text-center" style="width:25%"><a href ="freeDetail.do?board_id='+board.board_id+'">'+board.board_title+'</td>';
			content +='<td class="text-center col-md-1">'+board.user_id+'</td>';
			content +='<td class="text-center col-md-1">'+board.board_date+'</td>';
			content +='<td class="text-center col-md-1">'+board.board_views+'</td>';
			content +='</tr>';
		});
		//paging plugin
        $('#pagination').twbsPagination({
			startPage: data.currPage,
			totalPages: data.pages,
			visiblePages: $("#pagePerNum").val(),
			onPageClick: function(event,page){
				if(page != showPage){
					showPage=page;
					listCall(page);
				}
			}
		});
	} else {
		content +='<tr>';
		content +='<td colspan="5" class="text-center">데이터가 없습니다.</td>';
		content +='</tr>';
	}
	$('#list').empty();
	$('#list').append(content);
}

function buttonControl(loginId) {
	if(loginId == 'null') {
		$(".btnCtrl").addClass("none");
	}
}
</script>
</html>