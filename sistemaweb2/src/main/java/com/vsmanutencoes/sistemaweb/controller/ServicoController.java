package com.vsmanutencoes.sistemaweb.controller;

//import com.vsmanutencoes.sistemaweb.models.Material;
import com.vsmanutencoes.sistemaweb.models.Servico;
import com.vsmanutencoes.sistemaweb.service.MaterialService;
import com.vsmanutencoes.sistemaweb.service.ServicoService;

import java.security.Principal;
//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;
    
    @Autowired
    private MaterialService materialService;

    @GetMapping
    public String listarServicos(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String nome,
        Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);

        // Caso todos os filtros sejam nulos, traga todos os serviços
        if (id == null && nome == null) {
            model.addAttribute("servicos", servicoService.listarTodosServicos());
        } else {
            model.addAttribute("servicos", servicoService.filtrarServicos(id, nome));
        }
        return "servicos";
    }

    @GetMapping("/new")
    public String novoServicoForm(Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("servico", new Servico());
        model.addAttribute("materiais", materialService.listarTodosMateriais());
        return "servico-form";
    }

    @PostMapping("/save")
    public String salvarEquipamento(@ModelAttribute("servico") Servico servico
                                     //,@RequestParam List<Long> materialIds
                                ) {
        /*List<Material> materiaisSelecionados = materialService.buscarMateriaisPorIds(materialIds);
        
        servico.setMateriais(materiaisSelecionados);*/
        
        servicoService.salvarServico(servico);
        return "redirect:/servicos";
    }

    @GetMapping("/edit/{id}")
    public String editarServicoForm(@PathVariable("id") Long id, Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
    	Servico servico = servicoService.buscarServicoPorId(id);
    	model.addAttribute("servico", servico);
    	//model.addAttribute("materiais", materialService.listarTodosMateriais());
    	return "/servico-form";
    }

    @GetMapping("/delete/{id}")
    public String excluirServico(@PathVariable("id") Long id, Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
        servicoService.excluirServico(id);
        return "redirect:/servicos";
    }
}
