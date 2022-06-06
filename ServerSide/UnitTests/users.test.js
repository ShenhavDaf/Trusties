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
  User.deleteOne({ email: email }, (e0) => {
    User.deleteOne({ email: "friend_1@test.com" }, (e1) => {
      User.deleteOne({ email: "friend_2@test.com" }, (e2) => {
        User.deleteOne({ email: "friend_3@test.com" }, (e0) => {
          done();
        });
      });
    });
  });
});

afterAll((done) => {
  User.deleteOne({ email: email }, (e0) => {
    User.deleteOne({ email: "friend_1@test.com" }, (e1) => {
      User.deleteOne({ email: "friend_2@test.com" }, (e2) => {
        User.deleteOne({ email: "friend_3@test.com" }, (e0) => {
          mongoose.connection.close();
          done();
        });
      });
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
  let friend_1_ID = "";
  let friend_2_ID = "";
  let friend_3_ID = "";
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
      email: "friend_1@test.com",
      password: "123456",
      name: "friend_1 name (user test)",
      phone: "friend_1 phone (user test)",
    });
    expect(res1.statusCode).toEqual(200);
    friend_1_ID = res1.body.newUser._id;

    const zeroTOone = await request(app)
      .get("/user/addFriendToMyContacts/" + userId + "/" + friend_1_ID)
      .query({ myId: userId, hisId: friend_1_ID });
    // .set({ authorization: "JWT " + accessToken });
    expect(zeroTOone.statusCode).toEqual(200);
    expect(zeroTOone.body._id).toEqual(userId);

    /* --- --- --- --- --- --- --- --- --- --- */
    /* --- --- --- --- --- --- --- --- --- --- */

    const res2 = await request(app).post("/auth/register").send({
      email: "friend_2@test.com",
      password: "123456",
      name: "friend_2 name (user test)",
      phone: "friend_2 phone (user test)",
    });
    expect(res2.statusCode).toEqual(200);
    friend_2_ID = res2.body.newUser._id;

    const oneTOtwo = await request(app)
      .get("/user/addFriendToMyContacts/" + friend_1_ID + "/" + friend_2_ID)
      .query({ myId: friend_1_ID, hisId: friend_2_ID });
    // .set({ authorization: "JWT " + accessToken });
    expect(oneTOtwo.statusCode).toEqual(200);
    expect(oneTOtwo.body._id).toEqual(friend_1_ID);

    /* --- --- --- --- --- --- --- --- --- --- */
    /* --- --- --- --- --- --- --- --- --- --- */

    const res3 = await request(app).post("/auth/register").send({
      email: "friend_3@test.com",
      password: "123456",
      name: "friend_3 name (user test)",
      phone: "friend_3 phone (user test)",
    });
    expect(res3.statusCode).toEqual(200);
    friend_3_ID = res3.body.newUser._id;

    const twoTOthree = await request(app)
      .get("/user/addFriendToMyContacts/" + friend_2_ID + "/" + friend_3_ID)
      .query({ myId: friend_2_ID, hisId: friend_3_ID });
    // .set({ authorization: "JWT " + accessToken });
    expect(twoTOthree.statusCode).toEqual(200);
    expect(twoTOthree.body._id).toEqual(friend_2_ID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - add new post", async () => {
    const response = await request(app)
      .post("/post/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        email: "friend_1@test.com",
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
    expect(friends[0]).toEqual(friend_1_ID);
    expect(friends[1]).toEqual(friend_2_ID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - second circle", async () => {
    const response = await request(app)
      .get("/user/second/" + userId)
      .query({ id: userId });
    // .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const friends = response.body;
    expect(friends[0]).toEqual(friend_2_ID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - third circle", async () => {
    const response = await request(app)
      .get("/user/third/" + userId)
      .query({ id: userId });
    // .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const friends = response.body;
    expect(friends[0]).toEqual(friend_3_ID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - remove Friend From My Contacts", async () => {
    const response = await request(app)
      .get("/user/removeFriendFromMyContacts/" + userId + "/" + friend_1_ID)
      .query({ myId: userId, hisId: friend_1_ID });
    // .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    expect(response.body._id).toEqual(userId);
  });

  /* ******************************************** */
  /* ******************************************** */

  // test("user test - rate my help", async () => {
  //   const response = await request(app)
  //     .post("/user/rateMyHelp/" + userId)
  //     .send({ stars: "3" });
  //   // .set({ authorization: "JWT " + accessToken });
  //   expect(response.statusCode).toEqual(200);
  // });

  /* ******************************************** */
  /* ******************************************** */

  test("user test - get rating", async () => {
    const response = await request(app)
      .get("/user/getRating/" + userId)
      .query({ id: userId });
    // .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);

    const currUser = await request(app)
      .get("/auth/findById")
      .query({ id: userId });
    expect(currUser.statusCode).toEqual(200);

    if (currUser.body.rating)
      expect(currUser.body.rating).toEqual(response.body);
    else expect(response.body.rating).toEqual(0);
  });

  /* ******************************************** */
  /* ******************************************** */
});
