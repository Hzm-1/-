package RequestModel.controller;

import RequestModel.service.RequestService;
import jakarta.annotation.Resource;
import model.addNew.Request;
import model.addNew.RequestReceiver;
import model.message.AcceptFriendRequestDTO;
import model.message.Person;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {

    @Resource
    private RequestService requestService;

    @GetMapping("/list")
    public List<RequestReceiver> list(@RequestParam("toUserEmail")String toUserEmail) {
        return requestService.getRequest(toUserEmail);
    }

    @PostMapping("/accept")
    public void accept(@RequestBody AcceptFriendRequestDTO acceptFriendRequestDTO) {
        requestService.acceptRequest(acceptFriendRequestDTO.getPerson1(),acceptFriendRequestDTO.getPerson2());
    }
}
