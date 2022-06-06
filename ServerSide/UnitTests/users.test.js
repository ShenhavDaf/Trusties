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
    User.deleteOne({ email: "friend@test.com" }, (err) => {
      done();
    });
  });
});

afterAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    User.deleteOne({ email: "friend@test.com" }, (err) => {
      mongoose.connection.close();
      done();
    });
  });
});

describe("Testing User API", () => {
  const postTitle = "test post title (from unittest)";
  const postMessage = "this is my test post (from unittest)";
  const postCategory = "Car";
  const sender = "Adi";
  let accessToken = "";
  let userId = "";
  let friendID = "";
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

  test("user test - add Friend To My Contacts", async () => {
    const res1 = await request(app).post("/auth/register").send({
      email: "friend@test.com",
      password: "123456",
      name: "friend name (user test)",
      phone: "friend phone (user test)",
    });
    expect(res1.statusCode).toEqual(200);
    friendID = res1.body.newUser._id;

    const res2 = await request(app)
      .get("/user/addFriendToMyContacts/" + userId + "/" + friendID)
      .query({ myId: userId, hisId: friendID });
    // .set({ authorization: "JWT " + accessToken });
    expect(res2.statusCode).toEqual(200);
    expect(res2.body._id).toEqual(userId);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - add new post", async () => {
    const response = await request(app)
      .post("/post/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        email: "friend@test.com",
        title: postTitle,
        description: postMessage,
        category: postCategory,
      });
    expect(response.statusCode).toEqual(200);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - get all posts", async () => {
    const response = await request(app)
      .get("/user/getFeed/" + userId)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const firstPost = response.body[0];
    expect(firstPost.title).toEqual(postTitle);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - get friends list", async () => {
    const response = await request(app)
      .get("/user/" + userId)
      .query({ id: userId, circle: 2 });
    // .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const friends = response.body;
    expect(friends[0]).toEqual(friendID);
  });

  /* ******************************************** */
  /* ******************************************** */
});
