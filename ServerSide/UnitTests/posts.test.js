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
    Post.deleteOne(
      {
        title: "after edit - test post title (from unittest)",
        description: "after edit - this is my test post (from unittest)",
      },
      (err) => {
        done();
      }
    );
  });
});

afterAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    Post.deleteOne(
      {
        title: "after edit - test post title (from unittest)",
        description: "after edit - this is my test post (from unittest)",
      },
      (err) => {
        mongoose.connection.close();
        done();
      }
    );
  });
});

describe("Testing Post API", () => {
  const postTitle = "test post title (from unittest)";
  const postMessage = "this is my test post (from unittest)";
  const sender = "Adi";
  let accessToken = "";
  let userId = "";
  let postID;

  test("test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
    userId = response.body._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - get all posts", async () => {
    const response = await request(app)
      .get("/post/allPosts")
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - get post by id", async () => {
    const response = await request(app)
      .get("/post/" + postID)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const post = response.body;
    expect(post.title).toEqual(postTitle);
    expect(post.description).toEqual(postMessage);
    // expect(post2.sender).toEqual(sender);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - add new post", async () => {
    const response = await request(app)
      .post("/post/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        message: postMessage,
        // sender: new ObjectId("623f7e3cd4f056a7695c47d6"),
        title: postTitle,
        description: postMessage,
      });

    expect(response.statusCode).toEqual(200);
    const newPost = response.body.post;
    postID = newPost._id;
    expect(newPost.description).toEqual(postMessage);
    // expect(newPost.sender).toEqual(sender);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - edit post", async () => {
    const response = await request(app)
      .post("/post/edit/" + postID)
      .set({ authorization: "JWT " + accessToken })
      .send({
        title: "after edit - " + postTitle,
        description: "after edit - " + postMessage,
      });
    expect(response.statusCode).toEqual(200);
    const updatePost = response.body.post;
    expect(updatePost.description).toEqual("after edit - " + postMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("test - delete post", async () => {
    const response = await request(app)
      .post("/post/delete/" + postID)
      .set({ authorization: "JWT " + accessToken })
      .send();
    expect(response.statusCode).toEqual(200);
    const deletedPost = response.body.post;
    expect(deletedPost.isDeleted).toEqual(true);
  });
});
