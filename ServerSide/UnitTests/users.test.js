const app = require("../server");
const request = require("supertest");
const mongoose = require("mongoose");
const User = require("../Models/user_model");
const Post = require("../Models/post_model");

const email = "test@test.com";
const pwd = "123456";
const name = "test username (from unittest)";
const phone = "test phone (from unittest)";

beforeAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    done();
  });
});

afterAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    mongoose.connection.close();
    done();
  });
});

describe("Testing User API", () => {
  const postTitle = "test post title (from unittest)";
  const postMessage = "this is my test post (from unittest)";
  const postCategory = "Car";
  const sender = "Adi";
  let accessToken = "";
  let userId = "";
  let postID;

  test("user test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
    userId = response.body.newUser._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - add new post", async () => {
    const response = await request(app)
      .post("/post/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        email: email,
        title: postTitle,
        description: postMessage,
        category: postCategory,
      });
    expect(response.statusCode).toEqual(200);
    // postID = response.body.post._id;
    console.log("*************** " + response.body._id);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - get all posts", async () => {
    const response = await request(app)
      .get("/user/getFeed/" + userId)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + response.body.myFeed);
  });

  /* ******************************************** */
  /* ******************************************** */

  // test("post test - add new post", async () => {
  //   const response = await request(app)
  //     .post("/post/add")
  //     .set({ authorization: "JWT " + accessToken })
  //     .send({
  //       email: email,
  //       title: postTitle,
  //       description: postMessage,
  //       category: postCategory,
  //     });

  //   expect(response.statusCode).toEqual(200);
  //   const newPost = response.body.post;
  //   postID = newPost._id;
  //   expect(newPost.description).toEqual(postMessage);
  //   // expect(newPost.sender).toEqual(sender);
  // });

  /* ******************************************** */
  /* ******************************************** */
});
