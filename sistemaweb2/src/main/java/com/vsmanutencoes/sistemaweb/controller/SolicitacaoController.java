package com.vsmanutencoes.sistemaweb.controller;

import com.vsmanutencoes.sistemaweb.models.Cliente;
import com.vsmanutencoes.sistemaweb.models.Equipamento;
import com.vsmanutencoes.sistemaweb.models.Material;
import com.vsmanutencoes.sistemaweb.models.Servico;
import com.vsmanutencoes.sistemaweb.models.SolicitacaoOrcamento;
import com.vsmanutencoes.sistemaweb.service.ClienteService;
import com.vsmanutencoes.sistemaweb.service.EquipamentoService;
import com.vsmanutencoes.sistemaweb.service.MaterialService;
import com.vsmanutencoes.sistemaweb.service.ServicoService;
import com.vsmanutencoes.sistemaweb.service.SolicitacaoOrcamentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoOrcamentoService solicitacaoOrcamentoService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    private ServicoService servicoService;
    
    @Autowired
    private MaterialService materialService;

    @GetMapping
    public String listarSolicitacoes(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "empresa", required = false) String empresa,
            @RequestParam(value = "cnpj", required = false) String cnpj,
            @RequestParam(value = "equipamento", required = false) String equipamento,
            @RequestParam(value = "data", required = false) LocalDate data,
            Model model, Principal principal) {

        String username = principal.getName();
        model.addAttribute("username", username);

        List<SolicitacaoOrcamento> solicitacoes = solicitacaoOrcamentoService.filtrarSolicitacoes(nome, empresa, cnpj, equipamento, data);
        model.addAttribute("solicitacoes", solicitacoes);

        return "solicitacoes";
    }

    @GetMapping("/new")
    public String novoFormulario(Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("solicitacao", new SolicitacaoOrcamento());
        model.addAttribute("clientes", clienteService.listarTodosClientes());
        model.addAttribute("equipamentos", equipamentoService.listarTodosEquipamentos());
        model.addAttribute("servicos", servicoService.listarTodosServicos());
        model.addAttribute("materiais", materialService.listarTodosMateriais()); 

        return "solicitacao-form";
    }

    @PostMapping("/save")
    public String salvarSolicitacao(
        @ModelAttribute("solicitacao") SolicitacaoOrcamento solicitacao,
        @RequestParam List<Long> equipamentoIds,
        @RequestParam List<Long> servicoIds, 
        @RequestParam Long clienteId, 
        @RequestParam(required = false) List<Long> materialIds, 
        @RequestParam(required = false) List<Integer> quantidades) {
    	
    	List<Equipamento> equipamentoSelecionados = equipamentoService.buscarEquipamentosPorIds(equipamentoIds);
    	solicitacao.setEquipamentos(equipamentoSelecionados);

        List<Servico> servicoSelecionados = servicoService.buscarServicosPorIds(servicoIds);
    	solicitacao.setServicos(servicoSelecionados);

        List<Material> materialSelecionados = materialService.buscarMateriaisPorIds(materialIds);
    	solicitacao.setMateriais(materialSelecionados);
    	
    	Cliente cliente = clienteService.buscarClientePorId(clienteId);
        solicitacao.setCliente(cliente);
    	
    	LocalDateTime now = LocalDateTime.now();
        solicitacao.setData(now.toLocalDate());
        solicitacao.setHora(now.toLocalTime());
        
        solicitacaoOrcamentoService.calcularValorTotal(solicitacao, materialIds, quantidades);

        solicitacaoOrcamentoService.salvarSolicitacao(solicitacao);
        return "redirect:/solicitacoes";
    }

    @GetMapping("/edit/{id}")
    public String editarSolicitacaoForm(@PathVariable("id") Long id, Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
        SolicitacaoOrcamento solicitacao = solicitacaoOrcamentoService.buscarSolicitacaoPorId(id);
        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute("clientes", clienteService.listarTodosClientes());
        model.addAttribute("equipamentos", equipamentoService.listarTodosEquipamentos());
        model.addAttribute("materiais", materialService.listarTodosMateriais()); 
        return "solicitacao-form";
    }
    
    @GetMapping("/delete/{id}")
    public String excluirSolicitacao(@PathVariable("id") Long id, Model model, Principal principal
    ) {
        String username = principal.getName();
        model.addAttribute("username", username);
        solicitacaoOrcamentoService.excluirSolicitacao(id);
        return "redirect:/solicitacoes";
    }

}
