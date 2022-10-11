


const toggleSideBar=()=>
{ 
	
  if($(".sidebar").is(":visible"))
  {
  //true
    //band karna hai

        $(".sidebar").css("display","none");
        $(".content").css("margin-left", "0%");


  }
  else
  {
    //false
     //show karna hai
     $(".sidebar").css("display","block");
     $(".content").css("margin-left", "20%");

  }
};


const search=()=>
{
	
   let  query=$("#search-input").val();
  
   if(query=='')
   {
	
    $(".search-result").hide();

   }
   else
   {
     console.log(query);
    let url=`http://localhost:8083/user/search/${query}`;
                fetch(url).then((response)=>{
          
          
          
            return response.json();
                                      


                }).then((data)=>{
          //data
            console.log(data);
                    
                });
                
                    
    $(".search-result").show();

   }
}