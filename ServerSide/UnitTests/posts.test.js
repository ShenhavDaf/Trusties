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

describe("Testing Post API", () => {
  const postTitle = "test post title (from unittest)";
  const postMessage = "this is my test post (from unittest)";
  const postCategory = "Car";
  const sender = "Adi";
  const postPhoto =
    "https://play-lh.googleusercontent.com/V_P-I-UENK93ahkQgOWel8X8yFxjhOOfMAZjxXrqp311Gm_RBtlDXHLQhwFZN8n4aIQ";
  let accessToken = "";
  let userId = "";
  let postID;

  test("posts test - registration", async () => {
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

  test("posts test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - get all posts", async () => {
    const response = await request(app)
      .get("/post/allPosts")
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - add new post", async () => {
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
    postID = response.body._id;

    const newPost = (
      await request(app)
        .get("/post/" + postID)
        .set({ authorization: "JWT " + accessToken })
    ).body;

    expect(newPost.description).toEqual(postMessage);
    // expect(newPost.sender).toEqual(sender);
  });

  /* ******************************************** */
  /* ******************************************** */

  // test("posts test - add photo to post", async () => {
  //   const response = await request(app)
  //     .post("/post/addPhotosToPost" + postID)
  //     .query({ id: postID })
  //     .send({ photo: postPhoto })
  //     .set({ authorization: "JWT " + accessToken });
  //   expect(response.statusCode).toEqual(200);
  //   const post = response.body.post;
  //   expect(post.title).toEqual(postTitle);
  //   expect(post.photo).toEqual(postPhoto);
  // });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - get post by id", async () => {
    const response = await request(app)
      .get("/post/" + postID)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const post = response.body;
    expect(post.title).toEqual(postTitle);
    expect(post.description).toEqual(postMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - getMyPosts", async () => {
    const response = await request(app)
      .get("/post/MyPosts/" + userId)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const post = response.body[0];
    expect(post.title).toEqual(postTitle);
    expect(post.description).toEqual(postMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - edit post", async () => {
    const response = await request(app)
      .post("/post/edit/" + postID)
      .set({ authorization: "JWT " + accessToken })
      .send({
        title: "after edit - " + postTitle,
        description: "after edit - " + postMessage,
      });
    expect(response.statusCode).toEqual(200);

    const updatePost = (
      await request(app)
        .get("/post/" + response.body._id)
        .set({ authorization: "JWT " + accessToken })
    ).body;

    expect(updatePost.description).toEqual("after edit - " + postMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("posts test - delete post", async () => {
    const response = await request(app)
      .post("/post/delete/" + postID)
      .set({ authorization: "JWT " + accessToken })
      .send();
    expect(response.statusCode).toEqual(200);
    const deletedPost = response.body.post;
    expect(deletedPost.isDeleted).toEqual(true);
  });
});
