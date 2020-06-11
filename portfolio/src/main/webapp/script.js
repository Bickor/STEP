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
 * Gets the current messages and displays them as comments.
 */
async function getCurrentMessages() {
    const response = await fetch("/data");
    const message = await response.json();

    const commentList = document.getElementById("comments");
    
    // Clear everything before it.
    commentList.innerHTML = "";

    // Only display messages if there are any
    if (message != null) {

        // Display every message
        for (i = 0; i < message.length; i++) {
            if (message[i].nickname == "") {
                commentList.appendChild(createListElement(message[i].user + ": " + message[i].comment));
            } else {
                commentList.appendChild(createListElement(message[i].nickname + ": " + message[i].comment));
            }
            
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

/**
 * Creates an <a> element containing text.
 */
function createLinkElement(url, text) {
    const aElement = document.createElement("a");
    let link;
    
    aElement.title = text;
    link = document.createTextNode(text);
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
    const nav = document.getElementById("nav");
    const comments = document.getElementById("comments-form");
    loginItem.innerHTML = "";
    if (message["Loggedin"]) {
        // Show comment form.
        comments.classList.remove("hidden");
        loginItem.appendChild(createListElement("You are logged in as: " + message["User"]))
        nav.appendChild(createLinkElement(message["URL"], "Log Out"));
        updateNickname(message["Loggedin"]);
    } else {
        // Hide comment form.
        comments.classList.add("hidden");
        nav.appendChild(createLinkElement(message["URL"], "Login"));
        loginItem.appendChild(createListElement("You are not logged in."));
    }
}

/**
 * Handles the display depending if the user has a nickname.
 */
async function updateNickname(loggedIn) {
    if (loggedIn) {
        const response = await fetch("/user");
        const message = await response.json();

        const loginItem = document.getElementById("login");

        if (message["nickname"] == "") {
            loginItem.appendChild(createListElement("You don't have a nickname!"));
            loginItem.appendChild(createLinkElement("/nickname.html", "Add nickname!"));
        } else {
            loginItem.appendChild(createListElement("Your nickname is: " + message["nickname"]));
            loginItem.appendChild(createLinkElement("/nickname.html", "Change your nickname!"));
        }
    } else {
        return;
    }
}