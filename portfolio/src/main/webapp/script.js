// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var currentPicture = 1;
/**
 * Select a random picture.
 */
function randomPicture() {
    const pictures = ["landscape0.jpg", "landscape1.jpg", "landscape2.jpg",
        "landscape3.JPG", "landscape4.jpg", "landscape5.jpg", "landscape6.jpg",
        "landscape7.jpg", "landscape8.jpg", "landscape9.jpg"];

    var random = Math.floor(Math.random() * pictures.length);
    while(random == currentPicture) {
        random = Math.floor(Math.random() * pictures.length);
    }
    currentPicture = random;
    const picture = pictures[random];

    const pictureContainer = document.getElementById("landscapeImage");
    pictureContainer.src = "/images/" + picture;
    
}

/**
 * Gets the data and puts it in the portfolio.
 */
async function getMessage() {
  const response = await fetch("/data");
  const message = await response.json();

  // Only display messages if there are any.
  if (message.length != 0) {
      document.getElementById("messageContainer").innerText = message[Math.floor(Math.random() * message.length)];
  }
  
}

/**
 * Gets the current messages and displays them as comments.
 */
async function getCurrentMessages() {
    const response = await fetch("/data");
    const message = await response.json();

    const commentList = document.getElementById("comments");
    
    //TODO: Only add latest database entry (efficiency).
    // So I dont have to delete all of it every time.
    // Clear everything before it.
    commentList.innerHTML = "";

    // Only display messages if there are any
    if (message != null) {

        // Display every message
        for (i = 0; i < message.length; i++) {
            commentList.appendChild(createListElement(message[i]));
        }
    }
} 

/** 
 * Creates an <p> element containing text. 
 */
function createListElement(text) {
  const pElement = document.createElement("p");
  var textNode = document.createTextNode(text);
  pElement.appendChild(textNode);
  return pElement;
}

function createLinkElement(url, loggedIn) {
    const aElement = document.createElement("a");
    var link;
        console.log(isLoggedIn(loggedIn))
    
    if (isLoggedIn(loggedIn)) {
        aElement.title = "Log Out";
        link = document.createTextNode("Log Out");
    } else {
        aElement.title = "Login";
        link = document.createTextNode("Login");
    }
    aElement.appendChild(link);
    aElement.href = url;
    return aElement
}

/**
 * Function to check if the user is logged In.
 */
async function updateLogin() {
    const response = await fetch("/login");
    const message = await response.json();

    const loginItem = document.getElementById("login");
    const comments = document.getElementById("commentsForm");
    loginItem.innerHTML = "";

    if (isLoggedIn(message["Loggedin"])) {
        // Show comments
        comments.classList.remove("hidden");
        loginItem.appendChild(createListElement("You are logged in as: " + message["User"]))
        loginItem.appendChild(createListElement("Log out at: " + message["URL"]));
        loginItem.appendChild(createLinkElement(message["URL"], message["Loggedin"]));
    } else {
        // Hide comments
        comments.classList.add("hidden");
        loginItem.appendChild(createListElement("You are not logged in."));
        loginItem.appendChild(createListElement("Log in at: " + message["URL"]));
        loginItem.appendChild(createLinkElement(message["URL"], message["Loggedin"]));
    }
}

function isLoggedIn(boolean) {
    return boolean == "True";
}