const express = require("express");
const router = express.Router();

const Sos = require("../Controllers/soss");
const authenticate = require("../Common/auth_middleware");

/**
 * @swagger
 * components:
 *   schemas:
 *      Sos post:
 *        type: object
 *        required:
 *          - sender
 *          - title
 *          - message
 *          - time
 *          - role
 *          - status
 *          - area
 *          - address
 *        properties:
 *          sender:
 *                type: ObjectId
 *                ref: 'User'
 *                description: The id of user who send the post
 *          title:
 *                type: string
 *                description: The post title
 *          message:
 *                type: string
 *                description: The post text
 *          time:
 *                type: Date
 *                description: The post creation date
 *          role:
 *                type: String
 *                description: The post type - Standard question or SOS
 *          status:
 *                type: String
 *                description: The post status - open / in process / closed
 *          area:
 *                type: String
 *                description: The SOS area
 *          address:
 *                type: String
 *                description: The SOS address
 *        example:
 *          sender: '62264bf53dfa30aef30f6ca0'
 *          title: 'this is swagger test title'
 *          message: 'this is swagger test message'
 *          time: '2022-03-07 18:16:21'
 *          role: 'SOS'
 *          status: 'OPEN'
 *          area: 'Tel Aviv'
 *          address: 'herzl 20'
 */

router.get("/", authenticate, Sos.getSoss);
router.post("/add", authenticate, Sos.addSos);
router.get("/addSos", authenticate, Sos.getSoss);
// router.post("/getSoss", authenticate, Sos.getSoss);
router.post("/volunteer/:id", authenticate, Sos.volunteer);
router.post("/approveVolunteer/:id", authenticate, Sos.approveVolunteer);
router.post("/cancelVolunteer/:id", authenticate, Sos.cancelVolunteer);
router.post("/cancelApproveVolunteer/:id", authenticate, Sos.cancelApproveVolunteer);
router.get("/getSosVolunteers/:id", authenticate, Sos.getSosVolunteers);
router.post("/closeSos/:id", authenticate, Sos.closeSos);
router.get("/getApprovedVolunteer/:id", authenticate, Sos.getApprovedVolunteer);





module.exports = router;
