let editId = null;


// ADD STUDENT
function addStudent(){

const student = {
name: document.getElementById("name").value,
email: document.getElementById("email").value,
course: document.getElementById("course").value
};

if(editId == null){

fetch("http://localhost:8080/students",{
method:"POST",
headers:{
"Content-Type":"application/json"
},
body:JSON.stringify(student)
})
.then(res=>res.json())
.then(data=>{
alert("Student Added Successfully");
window.location.href="students.html";
});

}else{

fetch("http://localhost:8080/students/"+editId,{
method:"PUT",
headers:{
"Content-Type":"application/json"
},
body:JSON.stringify(student)
})
.then(res=>res.json())
.then(data=>{
alert("Student Updated Successfully");
window.location.href="students.html";
});

}

}


// DELETE STUDENT
function deleteStudent(id){

fetch("http://localhost:8080/students/"+id,{
method:"DELETE"
})
.then(()=>{
alert("Student Deleted");
location.reload();
});

}


// EDIT STUDENT
function editStudent(id){
window.location.href="addstudent.html?id="+id;
}


// PAGE LOAD
window.onload=function(){

// STUDENT TABLE PAGE
if(document.getElementById("studentTable")){

fetch("http://localhost:8080/students")
.then(res=>res.json())
.then(data=>{

let table="";

data.forEach(student=>{

table+=`
<tr>
<td>${student.id}</td>
<td>${student.name}</td>
<td>${student.email}</td>
<td>${student.course}</td>

<td>
<div id="qr-${student.id}"></div>
</td>

<td>
<button onclick="editStudent(${student.id})">Edit</button>
<button onclick="deleteStudent(${student.id})">Delete</button>
</td>

</tr>
`;

});

document.getElementById("studentTable").innerHTML=table;
data.forEach(student=>{

new QRCode(document.getElementById("qr-"+student.id),{
text: student.id.toString(),
width:100,
height:100
});

});
});

}


// EDIT MODE (ADD STUDENT PAGE)
if(document.getElementById("name")){

const params=new URLSearchParams(window.location.search);
const id=params.get("id");

if(id){

editId=id;

fetch("http://localhost:8080/students")
.then(res=>res.json())
.then(data=>{

	let student = data.find(s => s.id == id);

	if(student && document.getElementById("name")){

	document.getElementById("name").value = student.name;
	document.getElementById("email").value = student.email;
	document.getElementById("course").value = student.course;

	}



});

}

}

};